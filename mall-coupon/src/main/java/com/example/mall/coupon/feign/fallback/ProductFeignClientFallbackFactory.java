package com.example.mall.coupon.feign.fallback;

import com.example.mall.common.model.to.SkuInfoTo;
import com.example.mall.coupon.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public SkuInfoTo getInfo(Long skuId) {
                log.error("远程调用ProductFeignClient的getInfo处发生异常:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
