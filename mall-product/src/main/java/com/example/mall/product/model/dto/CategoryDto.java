package com.example.mall.product.model.dto;

import com.example.mall.product.model.po.Category;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryDto extends Category implements Serializable {
    private List<CategoryDto> children;

    @Override
    public String toString() {
        return super.toString();
    }
}
