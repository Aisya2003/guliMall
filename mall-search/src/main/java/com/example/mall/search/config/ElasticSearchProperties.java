package com.example.mall.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "mall.es")
public class ElasticSearchProperties {
    private String[] hosts;
    private String schema;
}
