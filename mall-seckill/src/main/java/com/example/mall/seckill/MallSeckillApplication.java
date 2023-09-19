package com.example.mall.seckill;

import com.example.mall.common.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({
        RedissonConfig.class,
        SpringSessionConfig.class,
        LocalDateTimeConfig.class,
        RabbitMQConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSeckillApplication.class, args);
    }

}
