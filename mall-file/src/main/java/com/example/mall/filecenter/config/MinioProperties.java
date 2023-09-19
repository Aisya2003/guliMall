package com.example.mall.filecenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mall.minio")
public class MinioProperties {
    private String endPoint;
    private String accessKey;
    private String accessSecret;
    private String simpleFileBucket;
    private String videoFileBucket;
}
