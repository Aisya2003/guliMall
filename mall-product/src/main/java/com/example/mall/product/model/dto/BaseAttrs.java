/**
  * Copyright 2023 bejson.com 
  */
package com.example.mall.product.model.dto;

import lombok.Data;

@Data
public class BaseAttrs {

    private Long attrId;
    private String attrValues;
    private int showDesc;

}