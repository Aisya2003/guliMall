package com.example.mall.order.feign.fallback;

import com.example.mall.order.feign.ProductFeignClient;
import com.example.mall.order.model.to.SpuInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {
    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public SpuInfoTo getSpuInfoBySkuId(Long skuId) {
                log.error("远程调用ProductFeignClient的getSpuInfoBySkuId出现错误,原因:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public List<String> brandNames(List<Long> brandIds) {
                log.error("远程调用ProductFeignClient的brandNames出现错误,原因:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public String url(Long spuId) {
                log.error("远程调用ProductFeignClient的url出现错误,原因:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public List<String> getSkuSaleAttrs(Long skuId) {
                log.error("远程调用ProductFeignClient的getSkuSaleAttrs出现错误,原因:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
