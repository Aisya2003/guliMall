package com.example.mall.member.feign;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.member.feign.fallback.OrderFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Component
@FeignClient(value = "mall-order", fallbackFactory = OrderFeignClientFallbackFactory.class)
public interface OrderFeignClient {
    @PostMapping("/order/order/inner/getmemberorder")
    Result<List<OrderAndOrderItemTo>> getMemberOrder(@RequestBody PageRequestParams params);
}
