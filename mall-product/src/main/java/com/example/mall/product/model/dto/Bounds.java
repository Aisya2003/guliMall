/**
 * Copyright 2023 bejson.com
 */
package com.example.mall.product.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}