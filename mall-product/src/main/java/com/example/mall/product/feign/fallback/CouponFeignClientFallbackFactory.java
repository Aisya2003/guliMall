package com.example.mall.product.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.product.feign.CouponFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CouponFeignClientFallbackFactory implements FallbackFactory<CouponFeignClient> {
    @Override
    public CouponFeignClient create(Throwable cause) {
        return new CouponFeignClient() {
            @Override
            public Result saveSpuBoundsInfo(SpuBoundsTo spuBoundsTo) {
                log.error("远程调用SpuCouponFeignClient的saveSpuBoundsInfo处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }

            @Override
            public Result saveSkuFullReductionInfo(SkuFullReductionTo skuFullReductionTo) {
                log.error("远程调用SpuCouponFeignClient的saveSkuReductionInfo处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
