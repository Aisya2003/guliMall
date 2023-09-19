package com.example.mall.cart.feign.fallback;

import com.example.mall.cart.feign.ProductFeignClient;
import com.example.mall.common.model.to.GetItemWeightTo;
import com.example.mall.common.model.to.SkuInfoTo;
import com.example.mall.common.model.to.UpdateCartItemPriceTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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

            @Override
            public List<String> getSkuSaleAttrs(Long skuId) {
                log.error("远程调用ProductFeignClient的getSkuSaleAttrs处发生异常:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public List<UpdateCartItemPriceTo> getPrice(Set<Long> skuIds) {
                log.error("远程调用ProductFeignClient的getPrice处发生异常:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public List<GetItemWeightTo> getWeight(Set<Long> skuIds) {
                log.error("远程调用ProductFeignClient的getWeight处发生异常:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
