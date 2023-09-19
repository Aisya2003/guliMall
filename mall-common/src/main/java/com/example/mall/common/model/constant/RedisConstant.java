package com.example.mall.common.model.constant;

public class RedisConstant {
    public static final String REDIS_URL_SPLIT_SYMBOL = "@";
    public static final String REDIS_URL_PREFIX = "redis://";

    public static final class Seckill {
        public static final String SECKILL_SUCCESS_PREFIX = "mall:seckill:";
        public static final String TIME_PATTERN = "yyyy/MM/dd HH/mm/ss";
        public static final String SECKILL_LOCK = "mall:seckill:lock";
        public static final String SESSION_PREFIX = "mall:seckill:session:";
        public static final String SESSION_SKU_PREFIX = "mall:seckill:skuRelation:info";
        public static final String SESSION_SKU_STOCK_SEMAPHORE_PREFIX = "mall:seckill:skuRelation:stock:";
    }

    public static final class Product {
        public static final String LOCK_PREFIX = "mall:product:lock:";
        public static final String CATALOG_KEY = "mall:product:catalog";
        public static final Long NULL_VALUE_EXPIRE_TIME = 1L * 60L;
        public static final Long CATALOG_EXPIRE_TIME = 1L * 60L * 60L * 24L;
    }

    public static final class Auth {
        public static final String CODE_TIME_SPLITER = "-";
        public static final Long SEND_CODE_EXPIRE_MILLIS = 60000L;
        public static final Integer CHECK_CODE_EXPIRE_MINUTE = 5;
        public static final String CHECK_CODE_KEY_PREFIX = "mall:auth:login:checkCode:";
    }

    public static final class Cart {
        public static final String USER_CART_PREFIX = "mall:cart:";
    }

    public static final class Order {
        public static final String ORDER_TOKEN_PREFIX = "mall:order:token:";
        public static final Integer ORDER_TOKEN_EXPIRE_MINUTES = 30;
    }
}
