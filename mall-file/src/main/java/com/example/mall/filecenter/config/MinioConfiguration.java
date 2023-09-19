package com.example.mall.filecenter.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndPoint())
                .credentials(properties.getAccessKey(), properties.getAccessSecret())
                .build();
    }

}
