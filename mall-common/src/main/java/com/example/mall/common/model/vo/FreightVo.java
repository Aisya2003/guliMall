package com.example.mall.common.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Data
public class FreightVo {
    private String detailAddress;
    private BigDecimal freight;
    private String receiver;
    private String province;
    private String city;
    private String region;
    private String phone;
    private String areacode;
    private String postCode;
    private String fullAddress;

}
