package com.example.mall.product.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.product.feign.CouponFeignClient;
import com.example.mall.product.feign.SearchFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SearchFeignClientFallbackFactory implements FallbackFactory<SearchFeignClient> {

    @Override
    public SearchFeignClient create(Throwable cause) {
        return new SearchFeignClient() {
            @Override
            public Result index(List<SkuUploadESTo> uploadESTos) {
                log.error("远程调用SearchFeignClient的index处发生异常:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
