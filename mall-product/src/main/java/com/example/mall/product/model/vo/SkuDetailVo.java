package com.example.mall.product.model.vo;

import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.product.model.po.SkuImages;
import com.example.mall.product.model.po.SkuInfo;
import com.example.mall.product.model.po.SpuInfoDesc;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class SkuDetailVo {
    private SkuInfo skuInfo;

    private List<SkuImages> skuImages;

    private List<SkuDetailSaleAttrVo> saleAttr;

    private SpuInfoDesc spuInfoDesc;

    private List<SpuDetailAttrGroupVo> groupAttrs;

    private boolean hasStock;

    private SeckillInfo seckillInfo;

    @Data
    public static class SeckillInfo {
        private String startTime;
        private String endTime;
        private Integer limit;
        private BigDecimal seckillPrice;
        private String randomCode;
        private String prompt;
        private Long promotionSessionId;
    }

    @Data
    @ToString
    public static class SkuDetailSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueAndSkuIdVo> attrValues;

        @Data
        public static class AttrValueAndSkuIdVo {
            private String attrValue;
            private String skuIds;
        }
    }

    @Data
    @ToString
    public static class SpuDetailAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    @ToString
    public static class SpuBaseAttrVo {
        private String attrName;
        private List<String> attrValue;

        public String getAttrValueForThymeleaf() {
            return attrValue.toString().replace("[", "").replace("]", "");
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = Arrays.asList(attrValue.split(";"));
        }
    }
}
