package com.example.mall.seckill;

import com.alibaba.fastjson2.JSON;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.seckill.service.SeckillService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootTest
class MallSeckillApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SeckillService seckillService;

    @Test
    void contextLoads() {
        System.out.println(JSON.toJSONString(LocalDateTime.now()));
    }

    @Test
    void test1() {
        Set<String> keys = stringRedisTemplate.keys(RedisConstant.Seckill.SESSION_SKU_PREFIX + "*");
        System.out.println(keys);
    }

    @Test
    void test3() {
        Result<SeckillSessionTo.SeckillSkuRelationTo> skuSeckillInfo =
                seckillService.getSkuSeckillInfo(34L);
        System.out.println(skuSeckillInfo.getData());
    }
    @Test
    @RabbitListener(queues = RabbitMQConstant.Seckill.SECKILL_ORDER_QUEUE_NAME)
    public void test(){
        System.out.println("asd");
    }
}
