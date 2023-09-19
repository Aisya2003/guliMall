package com.example.mall.common.config;

import com.example.mall.common.config.proeprty.ThreadPoolConfigProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class CustomThreadPoolConfiguration {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) {
        return new ThreadPoolExecutor(
                properties.getCoreSize(),
                properties.getMaxSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getBlockedQueueSize()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService(ThreadPoolConfigProperties properties) {
        return new ScheduledThreadPoolExecutor(
                properties.getScheduleCoreSize(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
