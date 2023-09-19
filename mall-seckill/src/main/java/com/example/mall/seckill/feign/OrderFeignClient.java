package com.example.mall.seckill.feign;

import com.example.mall.common.model.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@Component
@FeignClient("mall-order")
public interface OrderFeignClient {
    @GetMapping("/order/order/inner/payamount/{orderSn}")
    Result<BigDecimal> payAmount(@PathVariable("orderSn")String orderSn);
}
