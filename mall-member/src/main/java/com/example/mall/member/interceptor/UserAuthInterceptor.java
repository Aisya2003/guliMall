package com.example.mall.member.interceptor;

import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (new AntPathMatcher().match("/member/memberreceiveaddress/**", request.getRequestURI())
                || new AntPathMatcher().match("/member/member/**", request.getRequestURI())
                || new AntPathMatcher().match("/memberorder.html", request.getRequestURI())) {
            return true;
        }
        HttpSession session = request.getSession();
        MemberVo memberVo = (MemberVo) session.getAttribute(SystemConstant.SESSION_LOGIN_USER);
        if (memberVo == null) {
            //未登录
            response.sendRedirect(SystemConstant.MALL_AUTH_HOST);
            session.setAttribute("msg", "请先登录！");
            return false;
        }
        threadLocal.set(memberVo);
        return true;
    }
}
