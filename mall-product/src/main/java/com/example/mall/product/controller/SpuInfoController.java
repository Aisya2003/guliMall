package com.example.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.GetItemWeightTo;
import com.example.mall.product.model.dto.SpuRequestPageParams;
import com.example.mall.product.model.dto.SpuSaveDto;
import com.example.mall.product.model.po.SpuInfo;
import com.example.mall.product.service.SpuInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/product/spuinfo")
public class SpuInfoController {
    private final SpuInfoService spuInfoService;

    public SpuInfoController(SpuInfoService spuInfoService) {
        this.spuInfoService = spuInfoService;
    }
    @GetMapping("/inner/{skuId}/info")
    public SpuInfo getSpuInfoBySkuId(@PathVariable("skuId") Long skuId){
        return spuInfoService.getSpuInfoBySkuId(skuId);
    }
    @PostMapping("/inner/getweight")
    public List<GetItemWeightTo> getWeight(@RequestBody Set<Long> skuIds){
        return spuInfoService.getWeight(skuIds);
    }

    @PostMapping("/save")
    public Result saveComplexSpuInfo(@RequestBody SpuSaveDto dto) {
        spuInfoService.saveSpuInfo(dto);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result listWithConditions(PageRequestParams pageRequestParams, SpuRequestPageParams spuRequestPageParams) {
        return Result.ok(spuInfoService.listWithConditions(pageRequestParams, spuRequestPageParams));
    }
    @PostMapping("/{spuId}/up")
    public Result upLoadToES(@PathVariable("spuId")Long spuId){
        spuInfoService.upLoadToES(spuId);
        return Result.ok();
    }
}
