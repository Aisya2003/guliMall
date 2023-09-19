package com.example.mall.auth.web;

import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.auth.serivce.LoginOauth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class LoginOauth2Controller {
    private final LoginOauth2Service loginOauth2Service;

    @RequestMapping("/code/github")
    public String oauth2Callback(String code, String state, HttpSession session) {
        MemberVo memberVo = loginOauth2Service.loginByOauthGithub(code);
        session.setAttribute(SystemConstant.SESSION_LOGIN_USER, memberVo);
        return "redirect:" + SystemConstant.MALL_HOST;
    }
}
