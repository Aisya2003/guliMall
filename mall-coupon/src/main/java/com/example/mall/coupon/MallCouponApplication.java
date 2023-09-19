package com.example.mall.coupon;

import com.example.mall.common.config.LocalDateTimeConfig;
import com.example.mall.common.config.SentinelBlockUrlConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.example.mall.coupon.mapper")
@EnableFeignClients("com.example.mall.coupon.feign")
@Import({
        LocalDateTimeConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCouponApplication.class, args);
    }

}
