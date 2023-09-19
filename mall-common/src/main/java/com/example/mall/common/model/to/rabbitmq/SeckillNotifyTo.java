package com.example.mall.common.model.to.rabbitmq;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillNotifyTo {
    private String orderSn;
    private Long promotionSessionId;
    private Long skuId;
    private Long memberId;
    private String nickName;
    private BigDecimal seckillPrice;
    private Integer seckillCount;
    private String image;
    private String title;

    public BigDecimal getSeckillPrice() {
        return seckillPrice.multiply(BigDecimal.valueOf(seckillCount));
    }
}
