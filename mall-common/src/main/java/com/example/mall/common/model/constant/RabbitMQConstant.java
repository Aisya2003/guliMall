package com.example.mall.common.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RabbitMQConstant {
    @Getter
    @AllArgsConstructor
    public enum MqMessageStatusEnum {
        CREATE(0, "新建"),
        SENT(1, "已发送"),
        ERROR(2, "错误"),
        ARRIVE(3, "已到达");
        private final Integer code;
        private final String desc;
    }

    public static final Integer SEND_FAIL_DELAY_SECOND = 15;
    public static final String RESEND_CORRELATION_ID_PREFIX = "resend-";
    public static final String DEAD_EXCHANGE_ARGUMENT = "x-dead-letter-exchange";
    public static final String DEAD_EXCHANGE_ROUTING_KEY_ARGUMENT = "x-dead-letter-routing-key";
    public static final String MESSAGE_TTL_ARGUMENT = "x-message-ttl";

    public static class Seckill {
        public static final String EVENT_EXCHANGE_NAME = "order-event-exchange";
        public static final String SECKILL_ORDER_QUEUE_NAME = "order.seckill.order.queue";
        public static final String SECKILL_ORDER_QUEUE_ROUTING_KEY = "order.seckill.order";
        public static final String SECKILL_ORDER_PREFIX = "seckill-create-orderSn-";
    }

    public static class Order {
        public static final String CORRELATION_ID_CREATE_PREFIX = "order-create-order-";
        public static final String CORRELATION_ID_RELEASE_STOCK_PREFIX = "order-release-stock-order-";
        public static final Integer MESSAGE_TTL_MS = 600000;
        public static final String DELAY_QUEUE_NAME = "order.delay.order.queue";
        public static final String RELEASE_QUEUE_NAME = "order.release.order.queue";
        public static final String EVENT_EXCHANGE_NAME = "order-event-exchange";
        public static final String CREATE_ORDER_ROUTING_KEY = "order.create.order";
        public static final String RELEASE_ORDER_ROUTING_KEY = "order.release.order";
        public static final String RELEASE_STOCK_ROUTING_KEY = "order.release.stock";
    }

    public static class Ware {
        public static final String CORRELATION_ID_LOCKED_PREFIX = "ware-locked-orderTaskDetail-";
        //库存解锁补偿时间间隔
        public static final Integer MESSAGE_TTL_MS = 600000;

        public static final String EVENT_EXCHANGE_NAME = "stock-event-exchange";
        public static final String RELEASE_QUEUE_NAME = "stock.release.stock.queue";
        public static final String DELAY_QUEUE_NAME = "stock.delay.stock.queue";
        public static final String DEAD_EXCHANGE_ROUTING_KEY = "stock.release";
        public static final String LOCKED_ROUTING_KEY = "stock.locked";
        public static final String RELEASE_ROUTING_KEY = "stock.release.#";
        public static final String LOCKED_BINDING_ROUTING_KEY = "stock.locked.#";
    }

}
