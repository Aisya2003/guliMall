package com.example.mall.auth.serivce.impl;

import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.vo.MemberVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberFeignClient memberFeignClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberVo memberVo = memberFeignClient.login(authentication.getName());
        request.getSession().setAttribute(SystemConstant.SESSION_LOGIN_USER, memberVo);
        response.sendRedirect(SystemConstant.MALL_HOST);
    }
}
