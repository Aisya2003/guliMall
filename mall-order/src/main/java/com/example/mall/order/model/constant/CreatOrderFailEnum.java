package com.example.mall.order.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum CreatOrderFailEnum {
    ARGUMENT_FAULT(-1, "订单提交失败，请重新提交！"),
    SUCCESS(0, "操作成功"),
    CHECK_PRICE_FAULT(1, "订单价格发生变化！"),
    LOCK_STOCK_FAULT(2, "商品库存不足！");
    private final Integer code;
    private final String desc;
}
