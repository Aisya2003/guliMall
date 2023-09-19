package com.example.mall.order.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.LockStockTo;
import com.example.mall.order.feign.fallback.WareFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "mall-ware", fallbackFactory = WareFeignClientFallbackFactory.class)
public interface WareFeignClient {
    @PostMapping("/ware/waresku/order/lock")
    Result lockStock(@RequestBody LockStockTo lockStockTo);
}
