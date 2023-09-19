package com.example.mall.product.controller;

import com.example.mall.product.service.SkuSaleAttrValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/skusaleattrvalue")
public class SkuSaleAttrValueController {
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    @GetMapping("/inner/skusalevalues/{skuId}")
    public List<String> getSkuSaleAttrs(@PathVariable("skuId") Long skuId) {
        return skuSaleAttrValueService.getSkuSaleAttrsAsList(skuId);
    }
}
