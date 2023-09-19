package com.example.mall.search.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.search.feign.fallback.ProductFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "mall-product",fallbackFactory = ProductFeignClientFallbackFactory.class)
@Component
public interface ProductFeignClient {
    @GetMapping("/product/attr/inner/info/{attrId}")
    String attrInfo(@PathVariable("attrId") Long attrId);
    @GetMapping("/product/brand/inner/brand/names")
    List<String> brandNames(List<Long> brandIds);
}
