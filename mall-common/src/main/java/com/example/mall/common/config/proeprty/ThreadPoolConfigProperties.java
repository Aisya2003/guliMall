package com.example.mall.common.config.proeprty;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mall.thread.pool")
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer scheduleCoreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
    private Integer blockedQueueSize;
}
