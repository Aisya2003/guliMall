package com.example.mall.ware.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.ware.feign.fallback.OrderFeignClientFallbackFactory;
import com.example.mall.ware.feign.fallback.ProductFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "mall-order", fallbackFactory = OrderFeignClientFallbackFactory.class)
public interface OrderFeignClient {
    @GetMapping("/order/order/{orderSn}/order")
    Result<OrderTo> getOrderStatus(@PathVariable("orderSn") String orderSn);
}
