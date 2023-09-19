package com.example.mall.auth.feign;

import com.example.mall.auth.feign.fallback.MemberFeignClientFallbackFactory;
import com.example.mall.common.model.to.OauthLoginTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(value = "mall-member",fallbackFactory = MemberFeignClientFallbackFactory.class)
public interface MemberFeignClient {
    @PostMapping("/member/member/inner/register")
    Result register(@RequestBody RegisterVo registerVo);
    @PostMapping("/member/member/inner/getmember/{username}")
    MemberVo login(@PathVariable("username") String username);
    @PostMapping("/member/member/inner/oauthlogin")
    Result oauthLogin(@RequestBody OauthLoginTo oauthLoginTo);
}
