package com.example.mall.filecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({
        SentinelBlockHandlerConfig.class
})
public class MallFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallFileApplication.class, args);
    }

}
