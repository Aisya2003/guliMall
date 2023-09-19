package com.example.mall.cart.feign.fallback;

import com.example.mall.cart.feign.ProductFeignClient;
import com.example.mall.cart.feign.WareFeignClient;
import com.example.mall.common.model.to.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class WareFeignClientFallbackFactory implements FallbackFactory<WareFeignClient> {

    @Override
    public WareFeignClient create(Throwable cause) {
        return new WareFeignClient() {
            @Override
            public List<SkuHasStockTo> hasStockWithPrefetch(List<SkuHasStockPrefetchTo> skuHasStockPrefetchTos) {
                log.error("远程调用WareFeignClient的hasStock处发生异常:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
