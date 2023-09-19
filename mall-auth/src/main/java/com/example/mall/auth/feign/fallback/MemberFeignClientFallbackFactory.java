package com.example.mall.auth.feign.fallback;

import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.common.model.to.OauthLoginTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.vo.RegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class MemberFeignClientFallbackFactory implements FallbackFactory<MemberFeignClient> {
    @Override
    public MemberFeignClient create(Throwable cause) {
        return new MemberFeignClient() {
            @Override
            public Result register(RegisterVo registerVo) {
                log.error("远程调用MemberFeignClient的register方法出错，错误原因[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }

            @Override
            public MemberVo login(String username) {
                log.error("远程调用MemberFeignClient的login方法出错，错误原因[{}]", cause.getMessage());
                return null;
            }

            @Override
            public Result oauthLogin(OauthLoginTo oauthLoginTo) {
                log.error("远程调用MemberFeignClient的oauthLogin方法出错，错误原因[{}]", cause.getMessage());
                return Result.fail("服务被熔断");
            }
        };
    }
}
