package com.example.mall.order.model.vo;

import com.example.mall.order.model.po.Order;
import lombok.Data;

@Data
public class SubmitResultVo {
    private Integer responseCode;
    private Order order;
}
