package com.example.mall.ware.model.to;

import lombok.Data;

import java.util.List;

@Data
public class WareHasStockTo {
    private Long skuId;
    private Integer count;
    private List<Long> wareIdList;
}
