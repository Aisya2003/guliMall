package com.example.mall.seckill.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderVo {
    private String orderSn;
    private BigDecimal payAmount;
}
