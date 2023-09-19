package com.example.mall.common.model.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MemberPrice implements Serializable {
    private Long id;
    private String name;
    private BigDecimal price;
}
