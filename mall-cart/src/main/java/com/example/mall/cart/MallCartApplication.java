package com.example.mall.cart;

import com.example.mall.common.config.CustomThreadPoolConfiguration;
import com.example.mall.common.config.SentinelBlockUrlConfig;
import com.example.mall.common.config.SpringSessionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients(basePackages = "com.example.mall.cart.feign")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({
        SpringSessionConfig.class,
        CustomThreadPoolConfiguration.class,
        SentinelBlockUrlConfig.class
})
public class MallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCartApplication.class, args);
    }

}
