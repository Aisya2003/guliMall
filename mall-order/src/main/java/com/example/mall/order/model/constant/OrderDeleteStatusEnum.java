package com.example.mall.order.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderDeleteStatusEnum {
    //    删除状态【0->未删除；1->已删除】
    UNDELETED(0, "未删除"),
    DELETED(1, "已删除");
    private final Integer code;
    private final String desc;
}
