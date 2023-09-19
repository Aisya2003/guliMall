package com.example.mall.order.feign;

import com.example.mall.order.feign.fallback.CartFeignClientFallbackFactory;
import com.example.mall.order.feign.fallback.MemberFeignClientFallbackFactory;
import com.example.mall.order.model.vo.ConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(value = "mall-cart",fallbackFactory = CartFeignClientFallbackFactory.class)
public interface CartFeignClient {
    @GetMapping("/cart/inner/getcart")
    List<ConfirmVo.OrderItem> getCartItemList();
}
