package com.example.mall.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.common.model.to.rabbitmq.SeckillNotifyTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.seckill.feign.CouponFeignClient;
import com.example.mall.seckill.feign.OrderFeignClient;
import com.example.mall.seckill.interceptor.UserAuthInterceptor;
import com.example.mall.seckill.model.vo.SeckillOrderVo;
import com.example.mall.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.mall.common.model.constant.RabbitMQConstant.Seckill.SECKILL_ORDER_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService, SeckillService.Web {
    private final CouponFeignClient couponFeignClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final OrderFeignClient orderFeignClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void uploadProduct() {
        Result<List<SeckillSessionTo>> result = couponFeignClient.latest3DaySku();
        if (result == null || !result.isSuccess()) {
            return;
        }
        List<SeckillSessionTo> seckillSessionToList = JSON.parseArray(JSON.toJSONString(result.getData()), SeckillSessionTo.class);
        if (seckillSessionToList == null || seckillSessionToList.size() == 0) {
            return;
        }
        for (SeckillSessionTo seckillSessionTo : seckillSessionToList) {
            this.saveSeckillSession(seckillSessionTo);
            this.saveSeckillSkus(seckillSessionTo);
        }
    }

    @Override
    public Result<List<SeckillSessionTo.SeckillSkuRelationTo>> startAvailableSeckill() {
        List<SeckillSessionTo.SeckillSkuRelationTo> resultList = new ArrayList<>();
        //1.获取距离场次信息
        LocalDateTime now = LocalDateTime.now();
        Set<String> sessionKeys = stringRedisTemplate.keys(RedisConstant.Seckill.SESSION_PREFIX + "*");
        if (sessionKeys == null) {
            return null;
        }
        //2.获取每一个场次对应的商品信息
        for (String sessionKey : sessionKeys) {
            String timeStamp = sessionKey.replace(RedisConstant.Seckill.SESSION_PREFIX, "");
            LocalDateTime startTime = LocalDateTime.parse(timeStamp.split("-")[0],
                    DateTimeFormatter.ofPattern(RedisConstant.Seckill.TIME_PATTERN));
            LocalDateTime endTime = LocalDateTime.parse(timeStamp.split("-")[1],
                    DateTimeFormatter.ofPattern(RedisConstant.Seckill.TIME_PATTERN));
            if (now.isBefore(startTime) || now.isAfter(endTime)) {
                //不符合场次条件
                continue;
            }
            List<String> sessionSkuRedisIds = stringRedisTemplate.opsForList().range(sessionKey, -100, 100);
            if (sessionSkuRedisIds == null) {
                continue;
            }
            BoundHashOperations<String, String, Object> boundHashOps =
                    stringRedisTemplate.boundHashOps(RedisConstant.Seckill.SESSION_SKU_PREFIX);
            List<Object> jsonString = boundHashOps.multiGet(sessionSkuRedisIds);
            if (jsonString == null) {
                continue;
            }
            List<SeckillSessionTo.SeckillSkuRelationTo> seckillSkuRelationTos = jsonString
                    .stream()
                    .map(o -> JSON.parseObject(o.toString(), SeckillSessionTo.SeckillSkuRelationTo.class))
                    .collect(Collectors.toList());
            resultList.addAll(seckillSkuRelationTos);
        }

        return Result.ok(resultList);
    }

    @Override
    public Result<SeckillSessionTo.SeckillSkuRelationTo> getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, Object> boundHashOps
                = stringRedisTemplate.boundHashOps(RedisConstant.Seckill.SESSION_SKU_PREFIX);
        Set<String> keys = boundHashOps.keys();
        if (keys == null || keys.size() == 0) {
            return null;
        }
        for (String key : keys) {
            if (!key.matches("\\d-" + skuId)) {
                continue;
            }
            Object json = boundHashOps.get(key);
            if (json == null) {
                continue;
            }
            SeckillSessionTo.SeckillSkuRelationTo skuRelationTo =
                    JSON.parseObject(json.toString(), SeckillSessionTo.SeckillSkuRelationTo.class);
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(skuRelationTo.getEndTime())) {//结束
                continue;
            }
            String prompt = null;
            if (now.isBefore(skuRelationTo.getStartTime())) {
                prompt = "秒杀活动即将开始";
                skuRelationTo.setRandomCode(null);
            } else {
                prompt = "秒杀活动进行中~";
            }
            return Result.ok(skuRelationTo, prompt);
        }
        return null;
    }

    @Override
    public SeckillOrderVo seckillSku(String seckillId, Integer count, String token) {
        //1.校验数据合法性
        if (StringUtils.isBlank(token) || token.length() != 36) {
            return null;
        }

        MemberVo memberVo = UserAuthInterceptor.threadLocal.get();
        Long memberId = null;
        if (memberVo == null || (memberId = memberVo.getId()) == null) {
            return null;
        }

        BoundHashOperations<String, String, String> boundHashOps =
                stringRedisTemplate.boundHashOps(RedisConstant.Seckill.SESSION_SKU_PREFIX);
        String skuJson = boundHashOps.get(seckillId);
        if (StringUtils.isBlank(skuJson)) {
            return null;
        }

        SeckillSessionTo.SeckillSkuRelationTo skuRelationTo =
                JSON.parseObject(skuJson, SeckillSessionTo.SeckillSkuRelationTo.class);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = skuRelationTo.getStartTime();
        LocalDateTime endTime = skuRelationTo.getEndTime();
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return null;
        }

        if (count < 0 || count > skuRelationTo.getSeckillLimit()) {
            return null;
        }

        if (!token.equals(skuRelationTo.getRandomCode())
                || !seckillId.equals(skuRelationTo.getPromotionSessionId() + "-" + skuRelationTo.getSkuId())) {
            return null;
        }
        String memberSeckillRecordKey = RedisConstant.Seckill.SECKILL_SUCCESS_PREFIX + memberId + "-" + seckillId;
        Boolean setResult =
                stringRedisTemplate.opsForValue().setIfAbsent(memberSeckillRecordKey, count.toString(), Duration.between(now, endTime).plusMinutes(30));
        if (Boolean.FALSE.equals(setResult)) {
            String bought = stringRedisTemplate.opsForValue().get(memberSeckillRecordKey);
            if (StringUtils.isBlank(bought)) {
                //用户已购买，但是过期
                return null;
            }
            if (count + Integer.parseInt(bought) > skuRelationTo.getSeckillLimit()) {
                //再次抢购时超出了每人限购数量
                return null;
            }
        }
        RSemaphore stock = redissonClient.getSemaphore(RedisConstant.Seckill.SESSION_SKU_STOCK_SEMAPHORE_PREFIX + token);
        boolean seckillSuccess;
        seckillSuccess = stock.tryAcquire(count);
        if (!seckillSuccess) {
            return null;
        }
        String orderSn = IdWorker.getTimeId();
        //高并发情况下不保证消息的成功发送(少操作数据库)，保证的是消息发送后的可靠性
        this.trySendMessage(orderSn, count, memberId, skuRelationTo);
        SeckillOrderVo result = new SeckillOrderVo();
        result.setOrderSn(orderSn);
        Result<BigDecimal> bigDecimalResult = orderFeignClient.payAmount(orderSn);
        BigDecimal payAmount = null;
        if (bigDecimalResult == null || (payAmount = bigDecimalResult.castData(BigDecimal.class)) == null) {
            return result;
        }
        result.setPayAmount(payAmount);
        return result;
    }

    private void trySendMessage(String orderSn, Integer count, Long memberId, SeckillSessionTo.SeckillSkuRelationTo skuRelationTo) {
        SeckillNotifyTo seckillNotifyTo = new SeckillNotifyTo();
        seckillNotifyTo.setOrderSn(orderSn);
        seckillNotifyTo.setMemberId(memberId);
        seckillNotifyTo.setNickName(UserAuthInterceptor.threadLocal.get().getNickname());
        seckillNotifyTo.setSkuId(skuRelationTo.getSkuId());
        seckillNotifyTo.setPromotionSessionId(skuRelationTo.getPromotionSessionId());
        seckillNotifyTo.setSeckillCount(count);
        seckillNotifyTo.setSeckillPrice(skuRelationTo.getSeckillPrice());
        seckillNotifyTo.setImage(skuRelationTo.getSkuInfo().getSkuDefaultImg());
        seckillNotifyTo.setTitle(skuRelationTo.getSkuInfo().getSkuTitle());

        try {


            rabbitTemplate.convertAndSend(
                    RabbitMQConstant.Seckill.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Seckill.SECKILL_ORDER_QUEUE_ROUTING_KEY,
                    seckillNotifyTo,
                    new CorrelationData(SECKILL_ORDER_PREFIX + orderSn)
            );
        } catch (Exception e) {
            log.error("秒杀服务发送消息到消息队列发生异常[{}]", e.getMessage());
        }
    }

    private void saveSeckillSkus(SeckillSessionTo to) {
        BoundHashOperations<String, Object, Object> boundHashOps =
                stringRedisTemplate.boundHashOps(RedisConstant.Seckill.SESSION_SKU_PREFIX);
        Map<String, String> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastExpireTime = now;
        for (SeckillSessionTo.SeckillSkuRelationTo seckillSkuRelationTo : to.getSkuRelationList()) {
            String key = new StringJoiner("-")
                    .add(seckillSkuRelationTo.getPromotionSessionId().toString())
                    .add(seckillSkuRelationTo.getSkuId().toString())
                    .toString();
            if (Boolean.TRUE.equals(boundHashOps.hasKey(key))) {//已存在秒杀商品
                continue;
            }

            seckillSkuRelationTo.setStartTime(to.getStartTime());
            seckillSkuRelationTo.setEndTime(to.getEndTime());

            //获取最晚结束时间
            if (lastExpireTime.isBefore(to.getEndTime())) {
                lastExpireTime = to.getEndTime();
            }


            //秒杀业务开始时暴露code
            String code = UUID.randomUUID().toString();
            seckillSkuRelationTo.setRandomCode(code);
            map.put(key, JSON.toJSONString(seckillSkuRelationTo));

            //使用信号量作为库存量
            String semaphoreKey = RedisConstant.Seckill.SESSION_SKU_STOCK_SEMAPHORE_PREFIX + code;
            //过期时间为结束时间
            RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
            semaphore.trySetPermits(seckillSkuRelationTo.getSeckillCount().intValue());
            semaphore.expire(Duration.between(now, to.getEndTime()).plusMinutes(30).toMillis(), TimeUnit.MILLISECONDS);

        }
        if (map.size() == 0) {
            return;
        }
        //过期时间为最长结束时间
        boundHashOps.putAll(map);
        boundHashOps.expire(Duration.between(now, lastExpireTime).plusMinutes(30));
    }

    private void saveSeckillSession(SeckillSessionTo to) {
        LocalDateTime now = LocalDateTime.now();
        String startTime = to.getStartTime().format(DateTimeFormatter.ofPattern(RedisConstant.Seckill.TIME_PATTERN));
        String endTime = to.getEndTime().format(DateTimeFormatter.ofPattern(RedisConstant.Seckill.TIME_PATTERN));
        String key = RedisConstant.Seckill.SESSION_PREFIX + new StringJoiner("-").add(startTime).add(endTime);

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return;
        }

        List<String> skuIdList = to.getSkuRelationList()
                .stream()
                .map(seckillSkuRelationTo -> new StringJoiner("-")
                        .add(seckillSkuRelationTo.getPromotionSessionId().toString())
                        .add(seckillSkuRelationTo.getSkuId().toString())
                        .toString())
                .collect(Collectors.toList());
        stringRedisTemplate.opsForList().leftPushAll(key, skuIdList);
        stringRedisTemplate.expire(key, Duration.between(now, to.getEndTime().plusMinutes(30)));
    }
}
