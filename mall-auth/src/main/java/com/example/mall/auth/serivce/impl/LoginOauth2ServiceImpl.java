package com.example.mall.auth.serivce.impl;

import com.alibaba.fastjson2.JSON;
import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.auth.model.dto.AccessTokenDto;
import com.example.mall.auth.model.dto.Root;
import com.example.mall.common.model.to.OauthLoginTo;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.auth.serivce.LoginOauth2Service;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.utils.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class LoginOauth2ServiceImpl implements LoginOauth2Service {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    private final OAuth2ClientProperties properties;
    private final RestTemplate restTemplate;
    private final MemberFeignClient memberFeignClient;

    @Override
    public MemberVo loginByOauthGithub(String code) {
        RestTemplateUtil restTemplateUtil = new RestTemplateUtil(restTemplate);
        //获取github用户资源
        //1.获取token地址
        OAuth2ClientProperties.Provider githubProvider = properties.getProvider().get("github");
        String githubTokenUri = githubProvider.getTokenUri();
        //2.封装header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //3.封装请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("code", code);
        //4.获取令牌
        ResponseEntity<String> response = restTemplateUtil.post(githubTokenUri, httpHeaders, requestBody, String.class, Collections.emptyMap());
        String responseBody = response.getBody();
        AccessTokenDto accessTokenDto = JSON.parseObject(responseBody, AccessTokenDto.class);

        if (accessTokenDto == null) {
            throw new BadCredentialsException("授权失败！");
        }

        //访问资源服务器获取用户信息
        //1.获取资源服务器地址
        String userInfoUri = githubProvider.getUserInfoUri();
        //2.设置token
        httpHeaders.set("Authorization", "token " + accessTokenDto.getAccess_token());
        //3.获取授权用户信息
        response = restTemplateUtil.get(userInfoUri, httpHeaders, String.class, Collections.emptyMap());
        String userInfoJson = response.getBody();
        if (userInfoJson == null) {
            throw new BadCredentialsException("授权失败！");
        }
        Root root = JSON.parseObject(userInfoJson, Root.class);
        //4.保存用户信息并登录
        OauthLoginTo oauthLoginTo = new OauthLoginTo();
        oauthLoginTo.setGithubId(root.getId());
        oauthLoginTo.setUsername(root.getLogin());
        Result result = memberFeignClient.oauthLogin(oauthLoginTo);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMsg());
        }
        return JSON.parseObject(JSON.toJSONString(result.getData()), MemberVo.class);
    }
}
