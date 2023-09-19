package com.example.mall.coupon.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.coupon.service.SkuFullReductionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon/skufullreduction")
public class SpuFullReductionController {
    private final SkuFullReductionService skuFullReductionService;

    public SpuFullReductionController(SkuFullReductionService skuFullReductionService) {
        this.skuFullReductionService = skuFullReductionService;
    }

    @PostMapping("/saveto")
    Result saveSkuFullReductionInfo(@RequestBody SkuFullReductionTo skuFullReductionTo){
        skuFullReductionService.saveFullReductionDetail(skuFullReductionTo);
        return Result.ok();
    }
}
