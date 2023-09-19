package com.example.mall.search.model.constant;

public class ESConstant {
    public static final Integer DEFAULT_PAGE_SIZE = 5;
    public static final String PRODUCT_INDEX = "mall_product";
    public static final String PRICE_RANGE_SPLIT_SYMBOL = "_";
    public static final String ATTR_ID_SPLIT_SYMBOL = "_";
    public static final String SORT_SPLIT_SYMBOL = "_";
    public static final String ATTR_VALUE_SPLIT_SYMBOL = ":";
    public static final String SEARCH_KEYWORD_ATTR = "skuTitle";
    public static final String FILTER_CATALOG_ID = "catalogId";
    public static final String FILTER_BRAND_ID = "brandId";
    public static final String FILTER_HAS_STOCK = "hasStock";
    public static final String FILTER_PRICE = "skuPrice";
    public static final String FILTER_ATTRS = "attrs";
    public static final String FILTER_ATTRS_ID = "attrs.attrId";
    public static final String FILTER_ATTRS_VALUE = "attrs.attrValue.keyword";
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";
    public static final String HIGHLIGHT_PREFIX = "<b style='color: red'>";
    public static final String HIGHLIGHT_SUFFIX = "</b>";

    public static class Aggregation {
        public static final Integer DEFAULT_SIZE = 50;
        public static final String NAME_BRAND_ID = "brand_id_agg";
        public static final String NAME_BRAND_NAME = "brand_name_agg";
        public static final String NAME_BRAND_IMG = "brand_img_agg";
        public static final String NAME_CATALOG_ID = "catalog_id_agg";
        public static final String NAME_CATALOG_NAME = "catalog_name_agg";
        public static final String NAME_ATTR_PATH = "attr_agg";
        public static final String NAME_ATTR_ID = "attr_id_agg";
        public static final String NAME_ATTR_NAME = "attr_name_agg";
        public static final String NAME_ATTR_VALUE = "attr_value_agg";
        public static final String FIELD_ATTR_VALUE = "attrs.attrValue.keyword";
        public static final String FIELD_ATTR_NAME = "attrs.attrName";
        public static final String FIELD_ATTR_ID = "attrs.attrId";
        public static final String FIELD_ATTR_PATH = "attrs";
        public static final String FIELD_CATALOG_ID = "catalogId";
        public static final String FIELD_CATALOG_NAME = "catalogName.keyword";
        public static final String FIELD_BRAND_ID = "brandId";
        public static final String FIELD_BRAND_NAME = "brandName";
        public static final String FIELD_BRAND_IMG = "brandImg";
    }

}

