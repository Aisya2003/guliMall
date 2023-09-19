package com.example.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.MemberConstant;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.member.model.po.MemberLevel;
import com.example.mall.member.service.MemberLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mall.member.mapper.MemberMapper;
import com.example.mall.member.model.po.Member;
import com.example.mall.member.service.MemberService;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    private final MemberLevelService memberLevelService;

    @Override
    public Result register(RegisterVo registerVo) {
        String error = this.checkParamsAvailable(registerVo);
        if (StringUtils.isNotBlank(error)) {
            return Result.fail(error);
        }

        Member member = new Member();
        member.setUsername(registerVo.getUsername());
        member.setCreateTime(LocalDateTime.now());
        member.setEmail(registerVo.getMail());
        member.setPassword(registerVo.getPassword());
        member.setNickname(registerVo.getUsername());

        MemberLevel defaultLevel = memberLevelService.getOne(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getDefaultStatus, 1));
        member.setLevelId(defaultLevel.getId());

        this.save(member);
        return Result.ok();
    }

    @Override
    public Result oauthLogin(String username, String githubId) {
        //存在则绑定账号
        try {
            Member member = new Member();
            if (this.count(new LambdaQueryWrapper<Member>().eq(Member::getUsername, username)) > 0) {
                member.setGithubId(githubId);
                this.update(member, new LambdaQueryWrapper<Member>().eq(Member::getUsername, username));
            } else {
                //不存咋则新建
                member.setUsername(username);
                member.setNickname(username);
                member.setGithubId(githubId);
                member.setCreateTime(LocalDateTime.now());
                MemberLevel defaultLevel = memberLevelService.getOne(new LambdaQueryWrapper<MemberLevel>()
                        .eq(MemberLevel::getDefaultStatus, 1));
                member.setLevelId(defaultLevel.getId());
                this.save(member);
            }
            member = getOne(new LambdaQueryWrapper<Member>().eq(Member::getUsername, username));
            MemberVo memberVo = new MemberVo();
            BeanUtils.copyProperties(member, memberVo);
            return Result.ok(memberVo);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }



    private String checkParamsAvailable(RegisterVo registerVo) {
        if (this.count(new LambdaQueryWrapper<Member>()
                .eq(Member::getEmail, registerVo.getMail())) > 0) {
            return "mail-" + MemberConstant.MAIL_ADDR_DUPLICATE;
        }
        if (this.count(new LambdaQueryWrapper<Member>()
                .eq(Member::getUsername, registerVo.getUsername())) > 0) {
            return "username-" + MemberConstant.USERNAME_DUPLICATE;
        }
        return null;
    }
}