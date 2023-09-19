package com.example.mall.product.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.product.feign.CouponFeignClient;
import com.example.mall.product.feign.WareFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WareFeignClientFallbackFactory implements FallbackFactory<WareFeignClient> {

    @Override
    public WareFeignClient create(Throwable cause) {
        return new WareFeignClient() {
            @Override
            public List<SkuHasStockTo> hasStock(List<Long> skuIds) {
                log.error("远程调用WareFeignClient的hasStock处发生异常:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
