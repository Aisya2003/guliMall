package com.example.mall.product.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.product.feign.fallback.SeckillFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-seckill",fallbackFactory = SeckillFeignClientFallbackFactory.class)
public interface SeckillFeignClient {
    @GetMapping("/seckill/{skuId}/info")
    Result<SeckillSessionTo.SeckillSkuRelationTo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
