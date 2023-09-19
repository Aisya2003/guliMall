package com.example.mall.common.model.to;

import lombok.Data;

@Data
public class SkuHasStockPrefetchTo {
    private Long skuId;
    private Integer prefetch;
}
