package com.example.mall.auth;

import com.example.mall.common.config.LocalDateTimeConfig;
import com.example.mall.common.config.RestTemplateConfig;
import com.example.mall.common.config.SentinelBlockUrlConfig;
import com.example.mall.common.config.SpringSessionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients(basePackages = "com.example.mall.auth.feign")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({
        RestTemplateConfig.class,
        SpringSessionConfig.class,
        LocalDateTimeConfig.class,
        SentinelBlockUrlConfig.class,
})
public class MallAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthApplication.class, args);
    }

}
