package com.example.mall.common.model.to;

import lombok.Data;

@Data
public class LockResultTo {
    private Long skuId;
    private Integer count;
    private Boolean success;
}
