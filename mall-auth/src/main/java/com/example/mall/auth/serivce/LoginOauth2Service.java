package com.example.mall.auth.serivce;

import com.example.mall.common.model.vo.MemberVo;

public interface LoginOauth2Service {
    MemberVo loginByOauthGithub(String code);
}
