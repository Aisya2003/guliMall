package com.example.mall.member.feign.fallback;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.member.feign.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {
    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {
            @Override
            public Result<List<OrderAndOrderItemTo>> getMemberOrder(PageRequestParams params) {
                log.error("远程调用OrderFeignClient的getMemberOrder方法出错，原因[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
