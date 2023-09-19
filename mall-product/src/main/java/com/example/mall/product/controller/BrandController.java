package com.example.mall.product.controller;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/product/brand")
public class BrandController {
    private final BrandService brandService;


    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * 分页查询品牌
     *
     * @return
     */
    @GetMapping("/list")
    public Result brandList(PageRequestParams params) {
        return brandService.listByPage(params);
    }

    /**
     * 删除品牌
     */
    @PostMapping("/delete")
    public Result deleteBrands(@RequestBody Long[] ids) {
        brandService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok();
    }

    /**
     * 保存或者更新商品信息
     */
    @PostMapping("/appendOrUpdate")
    public Result appendOrUpdate(@RequestBody Brand brand) {
        brandService.saveOrUpdateDetail(brand);
        return Result.ok();
    }

    /**
     * 单个商品信息
     */
    @GetMapping("/{brandId}")
    public Result brandInfo(@PathVariable("brandId") Long brandId) {
        return Result.ok(brandService.getById(brandId));
    }

    @PostMapping("/inner/brand/names")
    public List<String> brandNames(@RequestBody List<Long> brandIds) {
        return brandService.listByIds(brandIds)
                .stream()
                .map(Brand::getName)
                .collect(Collectors.toList());
    }
}
