package com.example.mall.product.model.dto;

import com.example.mall.product.model.po.Attr;
import lombok.Data;

@Data
public class AttrRequestDto extends Attr {
    private Long attrGroupId;
}
