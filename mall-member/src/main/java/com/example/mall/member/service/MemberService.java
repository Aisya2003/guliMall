package com.example.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.member.model.po.Member;


/**
 * 会员
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
public interface MemberService extends IService<Member> {


    Result register(RegisterVo registerVo) throws IllegalArgumentException;


    Result oauthLogin(String username, String githubId);

}

