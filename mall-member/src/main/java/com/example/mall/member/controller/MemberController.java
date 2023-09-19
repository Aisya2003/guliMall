package com.example.mall.member.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.to.OauthLoginTo;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.common.model.vo.RegisterVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import com.example.mall.member.model.po.Member;
import com.example.mall.member.service.MemberService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 会员
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/member")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/inner/oauthlogin")
    public Result oauthLogin(@RequestBody OauthLoginTo oauthLoginTo) {
        return memberService.oauthLogin(oauthLoginTo.getUsername(), oauthLoginTo.getGithubId());
    }

    @PostMapping("/inner/getmember/{username}")
    public MemberVo login(@PathVariable("username") String username) {
        Member member = memberService.getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getUsername, username)
                .or()
                .eq(Member::getEmail, username));
        MemberVo memberVo = new MemberVo();
        BeanUtils.copyProperties(member,memberVo);
        return memberVo;
    }

    @PostMapping("/inner/register")
    public Result register(@RequestBody RegisterVo registerVo) {
        return memberService.register(registerVo);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params) {
        PageResult<Member> result = PageResultUtils.getPage(params, memberService.getBaseMapper(), Member.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        Member member = memberService.getById(id);

        return Result.ok(member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody Member member) {
        memberService.save(member);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Member member) {
        memberService.updateById(member);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
