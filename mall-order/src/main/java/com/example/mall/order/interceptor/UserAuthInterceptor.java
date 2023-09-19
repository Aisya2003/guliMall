package com.example.mall.order.interceptor;

import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.order.constant.AlipayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberVo> threadLocal = new ThreadLocal<>();
    private final AlipayProperties alipayProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getHeader("host").equals(alipayProperties.getNotify_url().replace("http://", ""))) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberVo memberVo = (MemberVo) session.getAttribute(SystemConstant.SESSION_LOGIN_USER);
        threadLocal.set(memberVo);
        if (new AntPathMatcher().match("/order/order/**", request.getRequestURI())) {
            return true;
        }

        if (memberVo == null) {
            //未登录
            response.sendRedirect(SystemConstant.MALL_AUTH_HOST);
            session.setAttribute("msg", "请先登录！");
            return false;
        }
        return true;
    }
}
