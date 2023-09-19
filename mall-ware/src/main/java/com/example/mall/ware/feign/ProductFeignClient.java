package com.example.mall.ware.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.ware.feign.fallback.ProductFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-product",fallbackFactory = ProductFeignClientFallbackFactory.class)
@Component
public interface ProductFeignClient {
    @GetMapping("/product/skuinfo/inner/info/{skuId}")
    Result getInfo(@PathVariable("skuId") Long skuId);
}
