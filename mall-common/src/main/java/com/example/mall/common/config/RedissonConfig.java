package com.example.mall.common.config;

import com.example.mall.common.model.constant.RedisConstant;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(@Value("${spring.redis.url}") String connectionUrl) {
        Config config = new Config();
        int splitIndex = connectionUrl.lastIndexOf(RedisConstant.REDIS_URL_SPLIT_SYMBOL);
        String redisUrl = RedisConstant.REDIS_URL_PREFIX + connectionUrl.substring(splitIndex + 1);
        String password = connectionUrl.substring(connectionUrl.lastIndexOf("/") + 1, splitIndex);
        config.useSingleServer().setAddress(redisUrl).setPassword(password);
        return Redisson.create(config);
    }

}
