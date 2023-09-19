package com.example.mall.seckill.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.seckill.feign.CouponFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class CouponFeignClientFallbackFactory implements FallbackFactory<CouponFeignClient> {
    @Override
    public CouponFeignClient create(Throwable cause) {
        return new CouponFeignClient() {
            @Override
            public Result<List<SeckillSessionTo>> latest3DaySku() {
                log.error("远程调用CouponFeignClient的latest3DaySku的方法异常:[{}]",cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
