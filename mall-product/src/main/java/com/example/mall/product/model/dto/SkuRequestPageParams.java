package com.example.mall.product.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuRequestPageParams {
    private Long catelogId;
    private Long brandId;
    private BigDecimal min;
    private BigDecimal max;
}
