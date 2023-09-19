package com.example.mall.seckill.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.seckill.feign.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.math.BigDecimal;
@Slf4j
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {
    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {
            @Override
            public Result<BigDecimal> payAmount(String orderSn) {
                log.error("远程调用OrderFeignClient的payAmount出现异常[{}]",cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
