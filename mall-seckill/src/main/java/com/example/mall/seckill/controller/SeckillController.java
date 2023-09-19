package com.example.mall.seckill.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {
    private final SeckillService seckillService;

    @GetMapping("/start")
    public Result<List<SeckillSessionTo.SeckillSkuRelationTo>> startAvailableSeckill() {
        return seckillService.startAvailableSeckill();
    }

    @GetMapping("/{skuId}/info")
    public Result<SeckillSessionTo.SeckillSkuRelationTo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        return seckillService.getSkuSeckillInfo(skuId);
    }

}
