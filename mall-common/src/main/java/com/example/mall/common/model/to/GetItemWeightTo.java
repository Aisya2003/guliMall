package com.example.mall.common.model.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetItemWeightTo {
    private Long skuId;
    private BigDecimal weight;
}
