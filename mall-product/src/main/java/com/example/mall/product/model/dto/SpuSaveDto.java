/**
 * Copyright 2023 bejson.com
 */
package com.example.mall.product.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveDto {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<List<String>> decript;
    private List<List<String>> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


}