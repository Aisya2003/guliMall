package com.example.mall.order.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.LockStockTo;
import com.example.mall.order.feign.WareFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class WareFeignClientFallbackFactory implements FallbackFactory<WareFeignClient> {
    @Override
    public WareFeignClient create(Throwable cause) {
        return new WareFeignClient() {
            @Override
            public Result lockStock(LockStockTo lockStockTo) {
                log.error("远程调用WareFeignClient的lockStock出现错误,原因:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
