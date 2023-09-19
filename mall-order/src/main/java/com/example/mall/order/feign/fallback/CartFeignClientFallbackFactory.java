package com.example.mall.order.feign.fallback;

import com.example.mall.order.feign.CartFeignClient;
import com.example.mall.order.feign.MemberFeignClient;
import com.example.mall.order.model.vo.ConfirmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class CartFeignClientFallbackFactory implements FallbackFactory<CartFeignClient> {

    @Override
    public CartFeignClient create(Throwable cause) {
        return new CartFeignClient() {
            @Override
            public List<ConfirmVo.OrderItem> getCartItemList() {
                log.error("远程调用CartFeignClient的getCartItemList出现错误,原因:[{}]", cause.getMessage());
                return null;
            }
        };
    }
}
