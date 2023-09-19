package com.example.mall.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignRequestInterceptorConfig {
    //传递请求的header
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes oldRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (oldRequest == null){
                    return;
                }
                requestTemplate.header("Cookie", oldRequest.getRequest().getHeader("Cookie"));
            }
        };
    }
}
