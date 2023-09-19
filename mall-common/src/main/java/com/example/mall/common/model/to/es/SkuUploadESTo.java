package com.example.mall.common.model.to.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuUploadESTo implements Serializable {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImage;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotStock;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;
    @Data
    public static class Attrs implements Serializable{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
