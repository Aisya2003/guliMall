package com.example.mall.product.model.dto;

import lombok.Data;

@Data
public class AttrResponseDto extends AttrRequestDto{
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;
}
