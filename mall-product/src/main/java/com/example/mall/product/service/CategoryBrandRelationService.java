package com.example.mall.product.service;

import com.example.mall.product.model.dto.CategoryBrandRelationDto;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.model.po.CategoryBrandRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 品牌分类关联 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelation> {

    void saveDetail(CategoryBrandRelationDto dto);

    /**
     * 关联表信息更新
     * @param brandId
     * @param name
     */
    void updateBrandInfo(Long brandId, String name);

    /**
     * 更新分类信息
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 查询商品信息
     * @param catId
     * @return
     */
    List<Brand> getBrandByCatId(Long catId);
}
