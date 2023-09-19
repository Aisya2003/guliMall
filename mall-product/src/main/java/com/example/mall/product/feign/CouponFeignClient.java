package com.example.mall.product.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.product.feign.fallback.CouponFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "mall-coupon",fallbackFactory = CouponFeignClientFallbackFactory.class)
public interface CouponFeignClient {
    @PostMapping("/coupon/spubounds/save")
    Result saveSpuBoundsInfo(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveto")
    Result saveSkuFullReductionInfo(@RequestBody SkuFullReductionTo skuFullReductionTo);
}
