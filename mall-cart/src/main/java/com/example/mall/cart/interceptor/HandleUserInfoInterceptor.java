package com.example.mall.cart.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.cart.model.to.UserTo;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class HandleUserInfoInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserTo userTo = new UserTo();
        MemberVo memberVo = (MemberVo) request.getSession().getAttribute(SystemConstant.SESSION_LOGIN_USER);
        if (memberVo != null) {
            userTo.setUserId(memberVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (ProductConstant.SESSION_USER_KEY.equals(cookie.getName())) {
                    userTo.setUserKey(cookie.getValue());
                    userTo.setHasKey(true);
                }
            }
        }
        if (StringUtils.isBlank(userTo.getUserKey())) {
            userTo.setHasKey(false);
            userTo.setUserKey(UUID.randomUUID().toString());
        }
        threadLocal.set(userTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserTo userTo = threadLocal.get();
        if (userTo.getHasKey()) {
            return;
        }
        String userKey = userTo.getUserKey();
        Cookie cookie = new Cookie(ProductConstant.SESSION_USER_KEY, userKey);
        cookie.setMaxAge(ProductConstant.SESSION_USER_KEY_EXPIRE_SECOND);
        cookie.setDomain(SystemConstant.SESSION_DOMAIN);
        response.addCookie(cookie);

    }
}
