package com.example.springdataredis.redisEx2;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// 스핀락 방식
// 락 획득할 때까지 폴링해야함
// SETNX 명령어 호출하는 방식
@Component
public class RedisLockRepository {
    private RedisTemplate<String, String> redisTemplate;

    public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(String key){
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key,"lock");

    }

    public Boolean unlock(String key){
        return redisTemplate
                .delete(key);
    }
}
