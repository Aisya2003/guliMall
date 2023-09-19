package com.example.mall.seckill.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.seckill.feign.fallback.CouponFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
@FeignClient(value = "mall-coupon",fallbackFactory = CouponFeignClientFallbackFactory.class)
public interface CouponFeignClient {
    @GetMapping("/coupon/seckillsession/inner/3DaySeckillSku")
    Result<List<SeckillSessionTo>> latest3DaySku();
}
