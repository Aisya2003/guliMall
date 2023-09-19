package com.example.mall.coupon.feign;

import com.example.mall.common.model.to.SkuInfoTo;
import com.example.mall.coupon.feign.fallback.ProductFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(value = "mall-product",fallbackFactory = ProductFeignClientFallbackFactory.class)
public interface ProductFeignClient {
    @GetMapping("/product/skuinfo/inner/skuinfo/{skuId}")
    SkuInfoTo getInfo(@PathVariable("skuId") Long skuId);
}
