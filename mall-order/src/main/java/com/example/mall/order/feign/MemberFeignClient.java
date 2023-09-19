package com.example.mall.order.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.vo.FreightVo;
import com.example.mall.order.feign.fallback.MemberFeignClientFallbackFactory;
import com.example.mall.order.model.vo.ConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(value = "mall-member", fallbackFactory = MemberFeignClientFallbackFactory.class)
public interface MemberFeignClient {
    @GetMapping("/member/memberreceiveaddress/inner/{memberId}/address")
    List<ConfirmVo.ReceiveAddress> memberAddressList(@PathVariable("memberId") Long memberId);

    @GetMapping("/member/memberreceiveaddress/{addrId}/fare")
    Result<FreightVo> getFare(@PathVariable("addrId") Long addrId);
}
