package com.example.mall.order.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitVo {
    private Long addrId;
    //在线支付
    private Integer payType = 1;
    private String orderToken;
    private BigDecimal payPrice;
    private String tips;
}
