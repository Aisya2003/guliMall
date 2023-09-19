package com.example.mall.search.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.search.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {
    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public String attrInfo(Long attrId) {
                log.error("远程调用ProductFeignClient的attrInfo处发生异常:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public List<String> brandNames(List<Long> brandIds) {
                log.error("远程调用ProductFeignClient的brandNames处发生异常:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
