package com.example.mall.auth.config;

import com.example.mall.auth.serivce.impl.LoginAuthenticationSuccessHandler;
import com.example.mall.common.model.constant.SystemConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.mall.auth.model.constant.HostConstant.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(expressionInterceptUrlRegistry -> {
                    expressionInterceptUrlRegistry
                            //表单登录地址
                            .antMatchers(FORM_LOGIN_PROCESS_URL).permitAll()
                            //注册地址
                            .antMatchers(REGISTER_REQUEST_URL).permitAll()
                            //注册页面
                            .antMatchers(REGISTER_PAGE).permitAll()
                            //重定向地址
                            .antMatchers(REDIRECT_URL).permitAll()
                            //登录地址
                            .antMatchers("/login.html/**").permitAll()
                            .antMatchers("/login/oauth2/code/github").permitAll()
                            .anyRequest().permitAll();
                })
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                    httpSecurityOAuth2LoginConfigurer
                            .loginPage(LOGIN_PAGE);
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginProcessingUrl(FORM_LOGIN_PROCESS_URL)
                            .loginPage(LOGIN_PAGE)
                            .successHandler(loginAuthenticationSuccessHandler)
                            .failureUrl(SystemConstant.MALL_AUTH_HOST + FAIL_PAGE);
                })
                .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                            .clearAuthentication(true)
                            .logoutSuccessUrl(SystemConstant.MALL_AUTH_HOST)
                            .logoutUrl(LOGOUT_URL)
                            .deleteCookies(SystemConstant.COOKIE_NAME)
                            .invalidateHttpSession(true);
                })
                .csrf().disable()
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
