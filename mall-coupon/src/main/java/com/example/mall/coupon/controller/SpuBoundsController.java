package com.example.mall.coupon.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.coupon.model.po.SpuBounds;
import com.example.mall.coupon.service.SpuBoundsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon/spubounds")
public class SpuBoundsController {
    private final SpuBoundsService spuBoundsService;

    public SpuBoundsController(SpuBoundsService spuBoundsService) {
        this.spuBoundsService = spuBoundsService;
    }

    @PostMapping("/save")
    public Result saveSpuBoundsInfo(@RequestBody SpuBounds spuBounds){
        spuBoundsService.save(spuBounds);
        return Result.ok();
    }
}
