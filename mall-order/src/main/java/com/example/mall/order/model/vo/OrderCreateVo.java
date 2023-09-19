package com.example.mall.order.model.vo;

import com.example.mall.order.model.po.Order;
import com.example.mall.order.model.po.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateVo {
    private Order order;
    private List<OrderItem> orderItem;
    private BigDecimal payPrice;
    private BigDecimal freight;
}
