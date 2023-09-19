package com.example.mall.order.feign;

import com.example.mall.order.feign.fallback.ProductFeignClientFallbackFactory;
import com.example.mall.order.model.to.SpuInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "mall-product",fallbackFactory = ProductFeignClientFallbackFactory.class)
public interface ProductFeignClient {
    @GetMapping("/product/spuinfo/inner/{skuId}/info")
    SpuInfoTo getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/brand/inner/brand/names")
    List<String> brandNames(@RequestBody List<Long> brandIds);
    @GetMapping("/product/spuinfodesc/inner/image/{spuId}")
    String url(@PathVariable("spuId") Long spuId);
    @GetMapping("/product/skusaleattrvalue/inner/skusalevalues/{skuId}")
    List<String> getSkuSaleAttrs(@PathVariable("skuId") Long skuId);
}