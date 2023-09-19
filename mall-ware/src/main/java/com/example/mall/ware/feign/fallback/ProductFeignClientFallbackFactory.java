package com.example.mall.ware.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.ware.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {
    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public Result getInfo(Long skuId) {
                log.error("远程调用ProductFeignClient的getInfo处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
