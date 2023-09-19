package com.example.mall.search;

import com.example.mall.common.config.SentinelBlockUrlConfig;
import com.example.mall.common.config.SpringSessionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({
        SpringSessionConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSearchApplication.class, args);
    }

}
