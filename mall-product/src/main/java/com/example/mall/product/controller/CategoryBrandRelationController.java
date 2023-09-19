package com.example.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.BrandDto;
import com.example.mall.product.model.dto.CategoryBrandRelationDto;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.model.po.CategoryBrandRelation;
import com.example.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product/categoryBrandRelation")
public class CategoryBrandRelationController {

    private final CategoryBrandRelationService categoryBrandRelationService;

    public CategoryBrandRelationController(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }
    @GetMapping("/brands/list")
    public Result getBrandList(Long catId){
        List<BrandDto> brandDtoList = categoryBrandRelationService.getBrandByCatId(catId)
                .stream()
                .map(brand -> {
                    BrandDto brandDto = new BrandDto();
                    brandDto.setBrandId(brand.getBrandId());
                    brandDto.setBrandName(brand.getName());
                    return brandDto;
                }).collect(Collectors.toList());

        return Result.ok(brandDtoList);
    }

    @GetMapping("/catelog/list")
    public Result list(@RequestParam("brandId") Long brandId) {
        return Result.ok(
                categoryBrandRelationService
                        .list(new QueryWrapper<CategoryBrandRelation>()
                                .eq("brand_id", brandId))
        );
    }

    /**
     * 保存信息
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public Result appendRelation(@RequestBody CategoryBrandRelationDto dto){
        categoryBrandRelationService.saveDetail(dto);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result removeRelations(@RequestBody Long id){
        categoryBrandRelationService.removeById(id);
        return Result.ok();
    }

}
