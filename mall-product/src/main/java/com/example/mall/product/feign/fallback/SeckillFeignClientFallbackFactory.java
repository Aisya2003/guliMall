package com.example.mall.product.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.product.feign.CouponFeignClient;
import com.example.mall.product.feign.SeckillFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignClientFallbackFactory implements FallbackFactory<SeckillFeignClient> {
    @Override
    public SeckillFeignClient create(Throwable cause) {
        return new SeckillFeignClient() {
            @Override
            public Result<SeckillSessionTo.SeckillSkuRelationTo> getSkuSeckillInfo(Long skuId) {
                log.info("远程调用SeckillFeignClient的getSkuSeckillInfo处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
