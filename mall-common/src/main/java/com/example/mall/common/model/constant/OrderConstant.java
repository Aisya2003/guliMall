package com.example.mall.common.model.constant;

public class OrderConstant {
    public static class OrderTableName {
        public static final String MEMBER_ID = "member_id";
    }

    public enum OrderStatusEnum {
        CREATE_NEW(0, "待付款"),
        PAYED(1, "已付款"),
        SENT(2, "已发货"),
        RECEIVED(3, "已完成"),
        CANCELLED(4, "已取消"),
        SERVICING(5, "售后中"),
        SERVICED(6, "售后完成");
        private final Integer code;
        private final String msg;

        OrderStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return msg;
        }
    }
}
