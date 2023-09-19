package com.example.mall.common.model.constant;

public class ProductConstant {
    public static final String SESSION_USER_KEY = "user-key";
    public static final Integer SESSION_USER_KEY_EXPIRE_SECOND = 60*60*24*7;
    public static final String ATTR_VALUE_SPLIT = ";";

    public enum SkuInfoTableNameEnum {
        SKU_INFO_PRICE("price", "商品价格");
        private final String tableName;
        private final String desc;

        SkuInfoTableNameEnum(String tableName, String desc) {
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

    public enum ProductAttrValueTableNameEnum {
        SPU_ID("spu_id", "商品id");
        private final String tableName;
        private final String desc;

        ProductAttrValueTableNameEnum(String tableName, String desc) {
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

    public enum AttrTableNameEnum {
        CATEGORY_ID("catelog_id", "三级分类id"),
        ATTR_TYPE("attr_type", "属性类型[0-销售属性，1-基本属性"),
        ATTR_ID("attr_id", "属性id");
        private final String tableName;
        private final String desc;

        AttrTableNameEnum(String tableName, String desc) {
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

    public enum AttrGroupTableNameEnum {
        CATEGORY_ID("catelog_id", "三级分类id");
        private final String tableName;
        private final String desc;

        AttrGroupTableNameEnum(String tableName, String desc) {
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

    public enum SpuInfoTableNameEnum {
        PUBLISH_STATUS("publish_status", "上架状态[0 - 下架，1 - 上架]"),
        BRAND_ID("brand_id", "品牌id"),
        CATEGORY_ID("catalog_id", "三级分类id");
        private final String tableName;
        private final String desc;

        SpuInfoTableNameEnum(String tableName, String desc) {
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

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "规格属性"),
        ATTR_TYPE_SALE(0, "销售属性"),
        ATTR_SEARCHABLE(1, "可被检索"),
        ATTR_UNSEARCHABLE(0, "不可检索");
        private final int code;
        private final String desc;

        AttrEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum ProductStatusEnum {
        NEW_CREATED(0, "新建"),
        UP(1, "上架中"),
        CANCEL(2, "下架");
        private final int code;
        private final String desc;

        ProductStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
