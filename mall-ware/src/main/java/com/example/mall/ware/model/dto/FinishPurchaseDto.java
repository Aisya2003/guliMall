package com.example.mall.ware.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinishPurchaseDto {
    private Long id;//采购单Id
    private List<FinishItems> items;
    @Data
    public static class FinishItems{
        private Long itemId;
        private Integer status;
        private String reason;
    }
}
