package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.mall.product.model.dto.CategoryBrandRelationDto;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.model.po.Category;
import com.example.mall.product.model.po.CategoryBrandRelation;
import com.example.mall.product.mapper.CategoryBrandRelationMapper;
import com.example.mall.product.service.BrandService;
import com.example.mall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationMapper, CategoryBrandRelation> implements CategoryBrandRelationService {
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Lazy
    public CategoryBrandRelationServiceImpl(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @Override
    public void saveDetail(CategoryBrandRelationDto dto) {
        CategoryBrandRelation categoryBrandRelation = new CategoryBrandRelation();
        BeanUtils.copyProperties(dto, categoryBrandRelation);

        Brand brand = brandService.getById(categoryBrandRelation.getBrandId());
        Category category = categoryService.getById(categoryBrandRelation.getCatelogId());

        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());

        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrandInfo(Long brandId, String name) {
        CategoryBrandRelation relation = new CategoryBrandRelation();
        relation.setBrandName(name);
        relation.setBrandId(brandId);

        this.update(relation, new LambdaUpdateWrapper<CategoryBrandRelation>()
                .eq(brandId != null, CategoryBrandRelation::getBrandId, brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelation relation = new CategoryBrandRelation();
        relation.setCatelogName(name);
        this.update(relation,
                new LambdaUpdateWrapper<CategoryBrandRelation>()
                        .eq(catId != null, CategoryBrandRelation::getCatelogId, catId));
    }

    @Override
    public List<Brand> getBrandByCatId(Long catId) {
        return list(new LambdaQueryWrapper<CategoryBrandRelation>()
                .eq(CategoryBrandRelation::getCatelogId, catId))
                .stream()
                .map(categoryBrandRelation -> brandService.getById(categoryBrandRelation.getBrandId()))
                .collect(Collectors.toList());

    }
}
