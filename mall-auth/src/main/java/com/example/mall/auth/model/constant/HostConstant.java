package com.example.mall.auth.model.constant;

import com.example.mall.common.model.constant.SystemConstant;

import javax.xml.soap.SAAJResult;

public class HostConstant {
    public static final String FORM_LOGIN_PROCESS_URL = "/form/login";
    public static final String REGISTER_REQUEST_URL = "/auth/register/**";
    public static final String REGISTER_PAGE = "/register.html/**";
    public static final String LOGIN_PAGE = SystemConstant.MALL_AUTH_HOST + "login.html";
    public static final String FAIL_PAGE = "error.html";
    public static final String REDIRECT_URL = "/login/oauth2/**";
    public static final String LOGOUT_URL = "/logout";
}
