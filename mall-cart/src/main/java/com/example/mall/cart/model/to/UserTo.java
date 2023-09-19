package com.example.mall.cart.model.to;

import lombok.Data;

@Data
public class UserTo {

    private Long userId;
    private String userKey;
    private Boolean hasKey = false;
}
