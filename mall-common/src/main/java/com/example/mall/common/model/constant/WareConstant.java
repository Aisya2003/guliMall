package com.example.mall.common.model.constant;

public class WareConstant {
    public enum WareSkuTableNameEnum {
        WARE_ID("ware_id", "库存id"),
        SKU_ID("sku_id", "sku的id"),
        STOCK("stock", "商品库存");
        private final String tableName;
        private final String desc;

        WareSkuTableNameEnum(String tableName, String desc) {
            this.tableName = tableName;
            this.desc = desc;
        }

        public String getTableName() {
            return tableName;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum PurchaseDetailTableNameEnum {
        PURCHASE_ID("purchase_id", "采购单id"),
        WARE_ID("ware_id", "库存id"),
        STATUS("status", "状态[0新建，1已分配，2正在采购，3已完成，4采购失败]");
        private final String tableName;
        private final String desc;

        PurchaseDetailTableNameEnum(String tableName, String desc) {
            this.tableName = tableName;
            this.desc = desc;
        }

        public String getTableName() {
            return tableName;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum PurchaseTableNameEnum {
        STATUS("status", "状态[0新建，1已分配，2正在采购，3已完成，4采购失败]");
        private final String tableName;
        private final String desc;

        PurchaseTableNameEnum(String tableName, String desc) {
            this.tableName = tableName;
            this.desc = desc;
        }

        public String getTableName() {
            return tableName;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum PurchaseStatusEnum {
        //状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
        PURCHASE_CREATED(0, "新建"),
        PURCHASE_ASSIGNED(1, "已分配"),
        PURCHASE_RECEIVE(2, "正在采购"),
        PURCHASE_FINISH(3, "已完成"),
        PURCHASE_EXCEPTION(4, "采购异常");
        private final int code;
        private final String msg;

        PurchaseStatusEnum(int code, String msg) {
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

    public enum PurchaseDetailStatusEnum {
        //状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
        PURCHASE_DETAIL_CREATED(0, "新建"),
        PURCHASE_DETAIL_ASSIGNED(1, "已分配"),
        PURCHASE_DETAIL_PROCESSING(2, "正在采购"),
        PURCHASE_DETAIL_FINISH(3, "已完成"),
        PURCHASE_DETAIL_FAILED(4, "采购失败");
        private final int code;
        private final String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
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

    public enum WareOrderTaskStatusEnum {
        CREATE(1, "创建成功"),
        SUCCESS(2, "处理完成");
        private final int code;
        private final String msg;

        WareOrderTaskStatusEnum(int code, String msg) {
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

    public enum WareOrderTaskDetailStatusEnum {
        LOCKED(1, "锁定成功"),
        UNLOCKED(2, "解锁成功"),
        SOLD(3, "扣减成功");
        private final int code;
        private final String msg;

        WareOrderTaskDetailStatusEnum(int code, String msg) {
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
