package com.example.mall.product.mapper;

import com.example.mall.product.model.po.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;


public interface CategoryMapper extends BaseMapper<Category> {
    @Select("select max(cat_level) from pms_category")
    int maxLevel();

}
