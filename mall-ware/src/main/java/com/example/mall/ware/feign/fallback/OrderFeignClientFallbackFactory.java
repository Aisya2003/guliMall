package com.example.mall.ware.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.ware.feign.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {
    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {
            @Override
            public Result<OrderTo> getOrderStatus(String orderSn) {
                log.error("远程调用OrderFeignClient的getOrderStatus处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
