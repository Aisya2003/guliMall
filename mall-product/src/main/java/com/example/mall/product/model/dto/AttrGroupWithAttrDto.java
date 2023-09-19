package com.example.mall.product.model.dto;

import com.example.mall.product.model.po.Attr;
import com.example.mall.product.model.po.AttrGroup;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrDto extends AttrGroup {
    private List<Attr> attrs;
}
