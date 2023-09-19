package com.example.mall.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.OrderConstant;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.common.model.exception.ThreadPoolException;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.*;
import com.example.mall.common.model.to.rabbitmq.SeckillNotifyTo;
import com.example.mall.common.model.vo.FreightVo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.order.constant.AlipayProperties;
import com.example.mall.order.feign.CartFeignClient;
import com.example.mall.order.feign.MemberFeignClient;
import com.example.mall.order.feign.ProductFeignClient;
import com.example.mall.order.feign.WareFeignClient;
import com.example.mall.order.interceptor.UserAuthInterceptor;
import com.example.mall.order.model.po.PaymentInfo;
import com.example.mall.order.model.to.SeckillOrder;
import com.example.mall.order.model.to.SpuInfoTo;
import com.example.mall.order.model.constant.CreatOrderFailEnum;
import com.example.mall.order.model.constant.OrderDeleteStatusEnum;
import com.example.mall.order.model.po.OrderItem;
import com.example.mall.order.model.vo.*;
import com.example.mall.order.model.po.Order;
import com.example.mall.order.mapper.OrderMapper;
import com.example.mall.order.service.MqMessageService;
import com.example.mall.order.service.OrderItemService;
import com.example.mall.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.order.service.PaymentInfoService;
import com.example.mall.order.utils.QRCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AlipayProperties.class)
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService, OrderService.Web {
    private final ThreadLocal<SubmitVo> threadLocal = new ThreadLocal<>();
    private final PaymentInfoService paymentInfoService;
    private final MqMessageService mqMessageService;
    private final OrderItemService orderItemService;
    private final AlipayProperties properties;
    private final MemberFeignClient memberFeignClient;
    private final WareFeignClient wareFeignClient;
    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final AlipayClient alipayClient;
    private final RedissonClient redissonClient;

    @Override
    public ConfirmVo confirm() {
        ConfirmVo confirmVo = new ConfirmVo();
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        //获取地址
        MemberVo memberVo = UserAuthInterceptor.threadLocal.get();
        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<ConfirmVo.ReceiveAddress> receiveAddresses = memberFeignClient.memberAddressList(memberVo.getId());
            confirmVo.setAddress(receiveAddresses);
        }, threadPoolExecutor);
        //获取选中商品
        CompletableFuture<Void> cartItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<ConfirmVo.OrderItem> orderItems = cartFeignClient.getCartItemList();
            confirmVo.setOrderItem(orderItems);
        }, threadPoolExecutor);
        //积分信息
        confirmVo.setIntegration(memberVo.getIntegration());

        //orderToken
        String orderToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(RedisConstant.Order.ORDER_TOKEN_PREFIX + memberVo.getId(),
                orderToken,
                Duration.ofMinutes(RedisConstant.Order.ORDER_TOKEN_EXPIRE_MINUTES));
        confirmVo.setOrderToken(orderToken);

        try {
            CompletableFuture.allOf(memberFuture, cartItemFuture).get();
        } catch (Exception e) {
            ThreadPoolException.error("确认订单地址、商品发生异常", e.getCause());
        }
        return confirmVo;
    }

    @Override
    @Transactional
    public SubmitResultVo submit(SubmitVo submitVo) {
        SubmitResultVo resultVo = new SubmitResultVo();
        //默认失败
        resultVo.setResponseCode(CreatOrderFailEnum.ARGUMENT_FAULT.getCode());
        //验证令牌
        MemberVo memberVo = UserAuthInterceptor.threadLocal.get();
        if (memberVo == null || memberVo.getId() == null) {
            return resultVo;
        }
        Long userId = memberVo.getId();
        String luaScript =
                "if redis.call('get',KEYS[1]) == ARGV[1] " +
                        "then return redis.call('del',KEYS[1]) " +
                        "else return 0 " +
                        "end";
        Long orderResult = stringRedisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class),
                Collections.singletonList(RedisConstant.Order.ORDER_TOKEN_PREFIX + userId),
                submitVo.getOrderToken());
        if (orderResult == null || orderResult == 0L) {
            return resultVo;
        }
        //验证成功
        //放大数据可用范围
        threadLocal.set(submitVo);
        //1.创建订单
        OrderCreateVo orderCreateVo = this.creatOrder();
        //2.验价
        BigDecimal payAmount = orderCreateVo.getOrder().getPayAmount();
        BigDecimal submitPrice = submitVo.getPayPrice();
        if (payAmount.subtract(submitPrice).abs().doubleValue() >= 0.01) {
            resultVo.setResponseCode(CreatOrderFailEnum.CHECK_PRICE_FAULT.getCode());
            return resultVo;
        }
        //3.保存订单
        this.saveOrderAndOrderItem(orderCreateVo);
        //4.锁定库存
        LockStockTo lockStockTo = new LockStockTo();
        lockStockTo.setOrderSn(orderCreateVo.getOrder().getOrderSn());
        lockStockTo.setLockList(orderCreateVo.getOrderItem()
                .stream()
                .map(orderItem -> {
                    LockStockTo.LockInfo lockInfo = new LockStockTo.LockInfo();
                    lockInfo.setCount(orderItem.getSkuQuantity());
                    lockInfo.setSkuId(orderItem.getSkuId());
                    lockInfo.setSkuName(orderItem.getSkuName());
                    return lockInfo;
                })
                .collect(Collectors.toList())
        );
        //锁定库存
        Result lockStock = wareFeignClient.lockStock(lockStockTo);
//        int i = 1 / 0;
        if (lockStock == null || !lockStock.isSuccess()) {
            resultVo.setResponseCode(CreatOrderFailEnum.LOCK_STOCK_FAULT.getCode());
            return resultVo;
        }
        //5.发送成功创建订单消息
        sendCreatOrderMessage(orderCreateVo.getOrder());

        resultVo.setOrder(orderCreateVo.getOrder());
        resultVo.setResponseCode(CreatOrderFailEnum.SUCCESS.getCode());
        return resultVo;
    }

    @Override
    public String payOrder(String orderSn) {
        try {
            Order order = this.getOne(new LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderSn, orderSn)
                    .eq(Order::getStatus, OrderConstant.OrderStatusEnum.CREATE_NEW.getCode()));
            if (order == null) {
                return null;
            }
            AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
            payModel.setOutTradeNo(order.getOrderSn());
            BigDecimal payAmount = order.getPayAmount();
            payAmount = payAmount.setScale(2, BigDecimal.ROUND_UP);
            payModel.setTotalAmount(payAmount.toString());
            payModel.setSubject(properties.getSubject());
            payModel.setProductCode(properties.getProductCode());
            payModel.setSellerId(properties.getSellerId());
            payModel.setEnablePayChannels(properties.getPayChannel());
            long expireSeconds = Duration.between(LocalDateTime.now(), order.getCreateTime()).getSeconds();
            payModel.setTimeoutExpress(String.valueOf(expireSeconds));

            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            request.setBizModel(payModel);
            request.setNotifyUrl(properties.getNotify_url() + properties.getNotifyPath());
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);

            if (response.isSuccess()) {
                return response.getBody();
            } else {
                throw new RuntimeException("请求支付失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void sendCreatOrderMessage(Order order) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY,
                    order,
                    new CorrelationData(RabbitMQConstant.Order.CORRELATION_ID_CREATE_PREFIX + order.getId())
            );
        } catch (Exception e) {
            log.error("消息发送失败[{}]", e.getMessage());
            mqMessageService.saveSendFailMessage(order,
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY);
        }
    }

    private void saveOrderAndOrderItem(OrderCreateVo orderCreateVo) {
        //保存order
        Order order = orderCreateVo.getOrder();
        order.setModifyTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        //保存orderItem
        orderItemService.saveBatch(orderCreateVo.getOrderItem());
    }

    private OrderCreateVo creatOrder() {
        OrderCreateVo orderCreateVo = new OrderCreateVo();
        //创建订单
        Order order = this.buildOrder();
        orderCreateVo.setOrder(order);
        //创建订单item
        List<OrderItem> orderItems = this.buildOrderItemList(order.getOrderSn(), order.getId());
        //计算价格、积分、折扣信息
        this.buildOrderDetail(order, orderItems);
        orderCreateVo.setOrderItem(orderItems);
        return orderCreateVo;
    }

    private void buildOrderDetail(Order order, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.size() == 0) {
            throw new IllegalArgumentException("订单项为空！");
        }
        Integer giftIntegration = 0;
        Integer giftGrowth = 0;
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal promotionAmount = new BigDecimal(0);
        BigDecimal couponAmount = new BigDecimal(0);
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderItems) {
            totalAmount = orderItem.getRealAmount().add(totalAmount);
            promotionAmount = orderItem.getPromotionAmount().add(promotionAmount);
            couponAmount = orderItem.getCouponAmount().add(couponAmount);
            integrationAmount = orderItem.getIntegrationAmount().add(integrationAmount);

            giftIntegration = orderItem.getGiftIntegration() + giftIntegration;
            giftGrowth = orderItem.getGiftGrowth() + giftGrowth;
        }
        order.setTotalAmount(totalAmount);
        totalAmount = totalAmount.add(order.getFreightAmount());
        order.setPayAmount(totalAmount);
        order.setPromotionAmount(promotionAmount);
        order.setIntegrationAmount(integrationAmount);
        order.setCouponAmount(couponAmount);

        order.setGrowth(giftGrowth);
        order.setIntegration(giftIntegration);
    }

    private Order buildOrder() {
        //生成订单号
        Order order = new Order();
        order.setOrderSn(IdWorker.getTimeId());
        //设置订单状态
        order.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        order.setDeleteStatus(OrderDeleteStatusEnum.UNDELETED.getCode());
        //获取运费
        Result<FreightVo> freight = memberFeignClient.getFare(threadLocal.get().getAddrId());
        if (freight.isSuccess()) {
            FreightVo freightVo = freight.castData(FreightVo.class);
            order.setFreightAmount(freightVo.getFreight());
            //设置收货信息
            order.setReceiverProvince(freightVo.getProvince());
            order.setReceiverCity(freightVo.getCity());
            order.setReceiverRegion(freightVo.getRegion());
            order.setReceiverDetailAddress(freightVo.getDetailAddress());
            order.setReceiverName(freightVo.getReceiver());
            order.setReceiverPhone(freightVo.getPhone());
            order.setReceiverPostCode(freightVo.getAreacode());
            order.setReceiverPostCode(freightVo.getPostCode());
        }

        //设置会员信息
        MemberVo memberVo = UserAuthInterceptor.threadLocal.get();
        if (memberVo == null) {
            return order;
        }
        order.setMemberId(memberVo.getId());
        order.setMemberUsername(memberVo.getNickname());
        return order;
    }

    private OrderItem buildOrderItem(ConfirmVo.OrderItem orderItem) {
        OrderItem result = new OrderItem();
        //spu信息
        SpuInfoTo spuInfo = productFeignClient.getSpuInfoBySkuId(orderItem.getSkuId());
        List<String> brandNames = productFeignClient.brandNames(Collections.singletonList(spuInfo.getBrandId()));
        String imageUrl = productFeignClient.url(spuInfo.getId());
        result.setSpuId(spuInfo.getId());
        result.setSpuName(spuInfo.getSpuName());
        result.setSpuBrand(brandNames.get(0));
        result.setSpuPic(imageUrl);

        result.setCategoryId(spuInfo.getCatalogId());

        //sku信息
        result.setSkuId(orderItem.getSkuId());
        result.setSkuName(orderItem.getTitle());
        result.setSkuPic(orderItem.getImage());
        result.setSkuPrice(orderItem.getPrice());
        result.setSkuAttrsVals(orderItem.getSkuAttr()
                .parallelStream()
                .collect(Collectors.joining(";")));
        result.setSkuQuantity(orderItem.getCount());
        //积分信息
        result.setGiftGrowth(orderItem.getPrice().intValue());
        result.setGiftIntegration(orderItem.getPrice().intValue());
        //价格信息
        result.setPromotionAmount(new BigDecimal(0));
        result.setCouponAmount(new BigDecimal(0));
        result.setIntegrationAmount(new BigDecimal(0));
        BigDecimal realAmount = result.getSkuPrice()
                .multiply(new BigDecimal(result.getSkuQuantity()))//总价
                .subtract(result.getPromotionAmount())
                .subtract(result.getCouponAmount())
                .subtract(result.getIntegrationAmount());
        result.setRealAmount(realAmount);

        return result;
    }

    private List<OrderItem> buildOrderItemList(String orderSn, Long orderId) {
        List<ConfirmVo.OrderItem> cartItemList = cartFeignClient.getCartItemList();
        if (cartItemList != null && !cartItemList.isEmpty()) {
            return cartItemList.stream()
                    .map(this::buildOrderItem)
                    .peek(orderItem -> {
                        orderItem.setOrderSn(orderSn);
                        orderItem.setOrderId(orderId);
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Result<OrderTo> getOrderByOrderSn(String orderSn) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderSn, orderSn));
        if (order == null) {
            return Result.ok();
        }
        OrderTo orderTo = new OrderTo();
        BeanUtils.copyProperties(order, orderTo);
        return Result.ok(orderTo);
    }

    @Override
    public void cancelOrder(Order order) {
        Order exist = this.getById(order.getId());
        if (exist == null || exist.getStatus().intValue() != OrderConstant.OrderStatusEnum.CREATE_NEW.getCode()) {
            log.info("订单不存在或订单已被处理[{}]", order);
            return;
        }
        //更新订单状态
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderConstant.OrderStatusEnum.CANCELLED.getCode());
        this.updateById(updateOrder);
    }

    @Override
    public Result<String> generateCode(String orderSn) {
        long exist = this.count(new LambdaQueryWrapper<Order>()
                .eq(StringUtils.isNotBlank(orderSn), Order::getOrderSn, orderSn));
        if (exist <= 0) {
            return null;
        }
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        try {
            return Result.ok(qrCodeUtil.createQRCode("http://" + properties.getIpAddress() + ":9000/order/order/payOrder/" + orderSn, 150, 150));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<List<OrderAndOrderItemTo>> getMemberOrder(int pageNum, int pageSize, String key) {
        Long memberId = UserAuthInterceptor.threadLocal.get().getId();
        List<OrderAndOrderItemTo> result = new ArrayList<>();


        LambdaQueryWrapper<Order> memberIdWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getMemberId, memberId);

        if (StringUtils.isNotBlank(key)) {
            memberIdWrapper = memberIdWrapper.and(orderLambdaQueryWrapper ->
                    orderLambdaQueryWrapper.in(Order::getOrderSn, key)
                            .or()
                            .like(Order::getMemberUsername, key)
                            .or()
                            .like(Order::getReceiverName, key)
                            .or()
                            .like(Order::getReceiverDetailAddress, key)
                            .or()
                            .like(Order::getReceiverProvince, key)
                            .or()
                            .like(Order::getReceiverRegion, key)
                            .or()
                            .like(Order::getReceiverCity, key));
        }

        List<OrderTo> memberOrders = this.list(memberIdWrapper)
                .stream()
                .map(order -> {
                    OrderTo orderTo = new OrderTo();
                    BeanUtils.copyProperties(order, orderTo);
                    return orderTo;
                })
                .sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()))
                .collect(Collectors.toList());
        int total = memberOrders.size();


        memberOrders = memberOrders.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .sorted(Comparator.comparingLong(OrderTo::getId))
                .collect(Collectors.toList());

        for (OrderTo memberOrder : memberOrders) {
            OrderAndOrderItemTo orderAndOrderItemTo = new OrderAndOrderItemTo();
            List<OrderItemTo> orderItemToList = this.orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderSn, memberOrder.getOrderSn()))
                    .stream()
                    .map(orderItem -> {
                        OrderItemTo orderItemTo = new OrderItemTo();
                        BeanUtils.copyProperties(orderItem, orderItemTo);
                        return orderItemTo;
                    })
                    .collect(Collectors.toList());
            orderAndOrderItemTo.setOrderItemToList(orderItemToList);
            orderAndOrderItemTo.setOrder(memberOrder);
            result.add(orderAndOrderItemTo);
        }
        return Result.ok(result, 200, String.valueOf(total));
    }

    @Override
    @Transactional
    public String handleSuccessOrder(PayAsyncVo payAsyncVo) {
        //保存交易流水
        this.savePaymentInfo(payAsyncVo);
        //更改订单状态
        if (payAsyncVo.getTrade_status().equalsIgnoreCase("TRADE_SUCCESS")
                || payAsyncVo.getTrade_status().equalsIgnoreCase("TRADE_FINISHED")) {
            Order updateOrder = new Order();
            updateOrder.setStatus(OrderConstant.OrderStatusEnum.PAYED.getCode());
            this.update(updateOrder, new LambdaQueryWrapper<Order>().eq(Order::getOrderSn, payAsyncVo.getOut_trade_no()));
            return null;
        }
        log.info("订单[{}]支付完毕", payAsyncVo.getOut_trade_no());
        return "success";
    }

    @Override
    public void closePayTrade(String orderSn) {
        AlipayTradeCloseRequest tradeCloseRequest = new AlipayTradeCloseRequest();

        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(orderSn);

        tradeCloseRequest.setBizModel(model);

        try {
            alipayClient.execute(tradeCloseRequest);
        } catch (AlipayApiException e) {
            throw new RuntimeException("关闭支付宝订单出现异常！");
        }
        log.info("关闭支付订单[{}]成功", orderSn);
    }

    @Override
    public SeckillOrder createSeckillOrder(SeckillNotifyTo to) {
        SubmitVo seckillSubmitVo = new SubmitVo();
        List<ConfirmVo.ReceiveAddress> receiveAddresses = memberFeignClient.memberAddressList(to.getMemberId());
        List<Long> defaultAddress = receiveAddresses.stream()
                .filter(ConfirmVo.ReceiveAddress::getDefaultStatus)
                .map(ConfirmVo.ReceiveAddress::getId)
                .collect(Collectors.toList());
        seckillSubmitVo.setAddrId(defaultAddress.get(0));
        threadLocal.set(seckillSubmitVo);
        //创建订单、修改订单号
        Order order = this.buildOrder();
        order.setOrderSn(to.getOrderSn());
        if (order.getMemberId() == null) {
            order.setMemberId(to.getMemberId());
        }
        if (StringUtils.isBlank(order.getMemberUsername())) {
            order.setMemberUsername(to.getNickName());
        }
        //创建订单项
        OrderItem orderItem = this.buildSeckillOrderItem(to);
        orderItem.setOrderSn(order.getOrderSn());
        orderItem.setOrderId(order.getId());
        //构建订单细节
        this.buildOrderDetail(order, Collections.singletonList(orderItem));
        //封装
        OrderCreateVo orderCreateVo = new OrderCreateVo();
        orderCreateVo.setOrder(order);
        orderCreateVo.setOrderItem(Collections.singletonList(orderItem));
        //保存到数据库
        this.saveOrderAndOrderItem(orderCreateVo);
        //准备消息
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrder(order);
        seckillOrder.setTo(to);
        return seckillOrder;
    }

    @Override
    public Result<BigDecimal> getPayAmountByOrderSn(String orderSn) {
        Order one = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderSn, orderSn));
        if (one == null) {
            return null;
        }
        BigDecimal payAmount = one.getPayAmount();
        if (payAmount != null) {
            return Result.ok(payAmount);
        }
        return null;
    }

    @Override
    public void cancelSeckillOrder(SeckillOrder seckillOrder) {
        SeckillNotifyTo seckillNotifyTo = seckillOrder.getTo();
        String orderSn = seckillNotifyTo.getOrderSn();
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderSn, orderSn));
        if (order == null || order.getStatus().intValue() != OrderConstant.OrderStatusEnum.PAYED.getCode()) {
            //不存在、已支付
            log.info("订单不存在或已支付");
            return;
        }
        //未支付且已到达时间
        String memberId = new StringJoiner("-")
                .add(seckillNotifyTo.getMemberId().toString())
                .add(seckillNotifyTo.getPromotionSessionId().toString())
                .add(seckillNotifyTo.getSkuId().toString())
                .toString();
        //删除用户记录
        String key = RedisConstant.Seckill.SECKILL_SUCCESS_PREFIX + memberId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
        //补回库存
        BoundHashOperations<String, String, String> boundHashOps =
                stringRedisTemplate.boundHashOps(RedisConstant.Seckill.SESSION_SKU_PREFIX);
        String infoKey = seckillNotifyTo.getPromotionSessionId() + "-" + seckillNotifyTo.getSkuId();
        SeckillSessionTo.SeckillSkuRelationTo skuRelationTo =
                JSON.parseObject(boundHashOps.get(infoKey), SeckillSessionTo.SeckillSkuRelationTo.class);
        assert skuRelationTo != null;
        String semaphoreKey = RedisConstant.Seckill.SESSION_SKU_STOCK_SEMAPHORE_PREFIX + skuRelationTo.getRandomCode();
        Integer seckillCount = seckillNotifyTo.getSeckillCount();
        redissonClient.getSemaphore(semaphoreKey).addPermits(seckillCount);
    }

    private OrderItem buildSeckillOrderItem(SeckillNotifyTo to) {
        ConfirmVo.OrderItem orderItem = new ConfirmVo.OrderItem();
        orderItem.setCount(to.getSeckillCount());
        orderItem.setSkuId(to.getSkuId());
        orderItem.setPrice(to.getSeckillPrice());
        orderItem.setSkuAttr(productFeignClient.getSkuSaleAttrs(to.getSkuId()));
        orderItem.setImage(to.getImage());
        orderItem.setTitle(to.getTitle());
        orderItem.setHasStock(true);
        return this.buildOrderItem(orderItem);
    }

    private void savePaymentInfo(PayAsyncVo vo) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAlipayTradeNo(vo.getTrade_no());
        paymentInfo.setOrderSn(vo.getOut_trade_no());
        paymentInfo.setPaymentStatus(vo.getTrade_status());
        paymentInfo.setCallbackContent(vo.getBody());
        paymentInfo.setCallbackTime(LocalDateTime.parse(vo.getNotify_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        paymentInfoService.save(paymentInfo);
    }
}
