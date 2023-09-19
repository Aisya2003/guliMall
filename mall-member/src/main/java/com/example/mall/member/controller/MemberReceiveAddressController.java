package com.example.mall.member.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.vo.FreightVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.member.model.po.MemberReceiveAddress;
import com.example.mall.member.service.MemberReceiveAddressService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 会员收货地址
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/memberreceiveaddress")
public class MemberReceiveAddressController {
    private final MemberReceiveAddressService memberReceiveAddressService;

    @GetMapping("/{addrId}/fare")
    public Result<FreightVo> getFare(@PathVariable("addrId")Long addrId){
        return memberReceiveAddressService.getFare(addrId);
    }

    @GetMapping("/inner/{memberId}/address")
    public List<MemberReceiveAddress> memberAddressList(@PathVariable("memberId") Long memberId) {
        return memberReceiveAddressService.list(new LambdaQueryWrapper<MemberReceiveAddress>()
                .eq(MemberReceiveAddress::getMemberId,memberId));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params) {
        PageResult<MemberReceiveAddress> result = PageResultUtils.getPage(params, memberReceiveAddressService.getBaseMapper(), MemberReceiveAddress.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        MemberReceiveAddress memberReceiveAddress = memberReceiveAddressService.getById(id);

        return Result.ok(memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberReceiveAddress memberReceiveAddress) {
        memberReceiveAddressService.save(memberReceiveAddress);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberReceiveAddress memberReceiveAddress) {
        memberReceiveAddressService.updateById(memberReceiveAddress);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
