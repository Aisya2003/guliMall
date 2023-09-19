package com.example.mall.auth.model.dto;

import lombok.Data;

@Data
public class AccessTokenDto {
    private String access_token;
    private String token_type;
    private String scope;
}
