package com.example.mall.cart.feign;

import com.example.mall.common.model.to.GetItemWeightTo;
import com.example.mall.common.model.to.SkuInfoTo;
import com.example.mall.common.model.to.UpdateCartItemPriceTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@Component
@FeignClient("mall-product")
public interface ProductFeignClient {
    @GetMapping("/product/skuinfo/inner/skuinfo/{skuId}")
    SkuInfoTo getInfo(@PathVariable("skuId") Long skuId);
    @GetMapping("/product/skusaleattrvalue/inner/skusalevalues/{skuId}")
    List<String> getSkuSaleAttrs(@PathVariable("skuId") Long skuId);
    @PostMapping("/product/skuinfo/inner/getprice")
    List<UpdateCartItemPriceTo> getPrice(@RequestBody Set<Long> skuIds);
    @PostMapping("/product/spuinfo/inner/getweight")
    List<GetItemWeightTo> getWeight(@RequestBody Set<Long> skuIds);
}
