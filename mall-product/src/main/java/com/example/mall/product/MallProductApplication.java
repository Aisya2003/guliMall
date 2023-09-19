package com.example.mall.product;

import com.example.mall.common.config.*;
import com.example.mall.product.service.impl.SkuSaleAttrValueServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@EnableCaching
@SpringBootApplication
@MapperScan(basePackages = "com.example.mall.product.mapper")
@EnableFeignClients(basePackages = "com.example.mall.product.feign")
@Import({
        LocalDateTimeConfig.class,
        SpringCacheCustomConfig.class,
        CustomThreadPoolConfiguration.class,
        SpringSessionConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class, args);
    }
}
