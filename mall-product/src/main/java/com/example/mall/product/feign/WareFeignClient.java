package com.example.mall.product.feign;

import com.example.mall.common.model.to.SkuHasStockTo;
import com.example.mall.product.feign.fallback.WareFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "mall-ware",fallbackFactory = WareFeignClientFallbackFactory.class)
public interface WareFeignClient {
    @PostMapping("/ware/waresku/inner/hasStock")
    List<SkuHasStockTo> hasStock(@RequestBody List<Long> skuIds);
}
