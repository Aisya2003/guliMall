package com.example.mall.cart.feign;

import com.example.mall.common.model.to.SkuHasStockPrefetchTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-ware")
public interface WareFeignClient {
    @PostMapping("/ware/waresku/inner/hasStock/prefetch")
    List<SkuHasStockTo> hasStockWithPrefetch(@RequestBody List<SkuHasStockPrefetchTo> hasStockPrefetchTos);
}
