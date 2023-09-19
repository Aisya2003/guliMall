package com.example.mall.common.model.constant;

public class CouponConstant {
    public static class SeckillSkuRelationTableField {
        public static final String PROMOTION_SESSION_ID = "promotion_session_id";
    }

    public enum MemberPriceEnum {
        ALLOW_ADD_OTHER(1, "可叠加其他优惠"),
        NOT_ALLOW_ADD_OTHER(0, "不可叠加其他优惠");
        private final int code;
        private final String msg;

        MemberPriceEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
