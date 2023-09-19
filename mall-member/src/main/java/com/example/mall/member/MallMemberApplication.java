package com.example.mall.member;

import com.example.mall.common.config.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.example.mall.member.mapper")
@EnableFeignClients(basePackages = "com.example.mall.member.feign")
@Import({
        SpringSessionConfig.class,
        LocalDateTimeConfig.class,
        FeignRequestInterceptorConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class, args);
    }
}
