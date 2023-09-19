package com.example.mall.common.model.to;

import lombok.Data;

import java.util.List;

@Data
public class OrderAndOrderItemTo {
    private OrderTo order;
    private List<OrderItemTo> orderItemToList;
}
