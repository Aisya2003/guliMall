package com.example.mall.common.model.to;

import lombok.Data;

import java.util.List;

@Data
public class LockStockTo {
    private String orderSn;
    private List<LockInfo> lockList;
    @Data
    public static class LockInfo{
        private Long skuId;
        private String skuName;
        private Integer count;
    }
}
