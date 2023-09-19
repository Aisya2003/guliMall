package com.example.mall.product.model.dto;

import com.example.mall.product.model.po.AttrGroup;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 属性分组
 * </p>
 *
 * @author zhuwenjie
 */
@Data
public class AttrGroupWithCatePathDto extends AttrGroup implements Serializable {
    private Long[] catelogIdPath;

}
