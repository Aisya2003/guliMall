package com.example.mall.order.feign.fallback;

import com.example.mall.common.model.result.Result;
import com.example.mall.order.feign.MemberFeignClient;
import com.example.mall.order.model.vo.ConfirmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class MemberFeignClientFallbackFactory implements FallbackFactory<MemberFeignClient> {
    @Override
    public MemberFeignClient create(Throwable cause) {
        return new MemberFeignClient() {
            @Override
            public List<ConfirmVo.ReceiveAddress> memberAddressList(Long memberId) {
                log.error("远程调用MemberClient的memberAddressList出现错误,原因:[{}]", cause.getMessage());
                return null;
            }

            @Override
            public Result getFare(Long addrId) {
                log.error("远程调用MemberClient的getFare出现错误,原因:[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
