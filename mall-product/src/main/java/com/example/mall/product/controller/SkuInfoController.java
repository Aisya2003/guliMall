package com.example.mall.product.controller;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuInfoTo;
import com.example.mall.common.model.to.UpdateCartItemPriceTo;
import com.example.mall.product.model.dto.SkuRequestPageParams;
import com.example.mall.product.model.po.SkuInfo;
import com.example.mall.product.service.SkuInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product/skuinfo")
public class SkuInfoController {
    private final SkuInfoService skuInfoService;

    public SkuInfoController(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    @PostMapping("/inner/getprice")
    public List<UpdateCartItemPriceTo> getPrice(@RequestBody Set<Long> skuIds) {
        return skuInfoService.listByIds(skuIds).stream()
                .map(skuInfo -> {
                    UpdateCartItemPriceTo updateCartItemPriceTo = new UpdateCartItemPriceTo();
                    updateCartItemPriceTo.setPrice(skuInfo.getPrice());
                    updateCartItemPriceTo.setSkuId(skuInfo.getSkuId());
                    return updateCartItemPriceTo;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/list")
    public Result listWithCondition(PageRequestParams pageRequestParams, SkuRequestPageParams skuRequestPageParams) {
        return Result.ok(skuInfoService.listWithCondition(pageRequestParams, skuRequestPageParams));
    }

    @GetMapping("/inner/info/{skuId}")
    public Result getInfo(@PathVariable("skuId") Long skuId) {
        return Result.ok(skuInfoService.getById(skuId).getSkuName());
    }

    @GetMapping("/inner/skuinfo/{skuId}")
    public SkuInfoTo getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        SkuInfoTo skuInfoTo = new SkuInfoTo();
        BeanUtils.copyProperties(skuInfo, skuInfoTo);
        skuInfo = null;
        return skuInfoTo;
    }
}
