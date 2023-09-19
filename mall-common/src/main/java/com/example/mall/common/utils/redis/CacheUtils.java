package com.example.mall.common.utils.redis;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.RedisConstant;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CacheUtils<R> {
    private final StringRedisTemplate stringRedisTemplate;
    private boolean hasLock = false;
    private RedissonClient redissonClient;
    private String lockKey;

    public void setRedissonClient(RedissonClient redissonClient, String key) {
        this.redissonClient = redissonClient;
        this.lockKey = key;
    }


    public CacheUtils(StringRedisTemplate stringRedisTemplate, boolean hasLock) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.hasLock = hasLock;
    }

    public R getCache(String key, Long expireTime, TimeUnit timeUnit, Supplier<R> queryFromDataSource) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseObject(json, new TypeReference<R>() {

            }.getType());
        } else {
            R r = null;
            //查询数据库建立缓存
            if (hasLock && this.redissonClient != null) {//加锁
                RLock lock = redissonClient.getLock(lockKey);
                lock.lock();
                try {
                    //doubleCheck
                    json = stringRedisTemplate.opsForValue().get(key);
                    if (StringUtils.isNotBlank(json)) {
                        return JSON.parseObject(json, new TypeReference<R>() {

                        }.getType());
                    } else {
                        //缓存查询到的值
                        return buildNullableCache(key, expireTime, timeUnit, queryFromDataSource);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                return buildNullableCache(key, expireTime, timeUnit, queryFromDataSource);
            }
        }
    }

    private R buildNullableCache(String key, Long expireTime, TimeUnit timeUnit, Supplier<R> queryFromDataSource) {
        R r;
        r = queryFromDataSource.get();
        //保存
        if (r == null) {
            //缓存空值
            stringRedisTemplate.opsForValue().set(key, "", RedisConstant.Product.NULL_VALUE_EXPIRE_TIME, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(r), expireTime, timeUnit);
        }
        return r;
    }

    public R getCache(String key, Supplier<R> buildCache) {
        return this.getCache(key, -1L, TimeUnit.SECONDS, buildCache);
    }
}
