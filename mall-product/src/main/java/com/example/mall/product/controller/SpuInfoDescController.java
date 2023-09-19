package com.example.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.model.po.SpuInfoDesc;
import com.example.mall.product.service.SpuInfoDescService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/spuinfodesc")
public class SpuInfoDescController {
    private final SpuInfoDescService spuInfoDescService;

    @GetMapping("/inner/image/{spuId}")
    public String url(@PathVariable("spuId") Long spuId) {
        return spuInfoDescService.getOne(new LambdaQueryWrapper<SpuInfoDesc>()
                .eq(SpuInfoDesc::getSpuId, spuId)).getDecript();
    }
}
