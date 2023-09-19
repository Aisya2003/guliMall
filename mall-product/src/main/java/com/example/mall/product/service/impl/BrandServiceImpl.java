package com.example.mall.product.service.impl;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.mapper.BrandMapper;
import com.example.mall.product.service.BrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.product.service.CategoryBrandRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
    private final CategoryBrandRelationService categoryBrandRelationService;

    public BrandServiceImpl(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    @Override
    public Result listByPage(PageRequestParams params) {
        PageResult<Brand> pageResult = PageResultUtils
                .getPage(params, baseMapper, Brand.class);
        return Result.ok(pageResult);
    }

    @Override
    @Transactional
    public void saveOrUpdateDetail(Brand brand) {
        Long brandId = brand.getBrandId();
        if (this.getById(brandId) != null) {//更新
            this.updateById(brand);
            categoryBrandRelationService.updateBrandInfo(brandId, brand.getName());
            //TODO 更新其他关联表
        } else {//保存
            this.save(brand);
        }
    }
}
