package com.example.springdataredis.redisEx2;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

// 스핀락 방식
// 락 획득할 때까지 폴링해야함
// SETNX 명령어 호출하는 방식
@Component
public class RedisLockRepository {
    private RedisTemplate<String, String> redisTemplate;

    public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(String key) throws InterruptedException {
        System.out.println("lock:"+key);
        long endTime = System.currentTimeMillis()+ 2000;

        while(endTime > System.currentTimeMillis()){
            Boolean isLocked= redisTemplate
                                .opsForValue()
                                .setIfAbsent(key,"lock", Duration.ofSeconds(10));
            if(Boolean.TRUE.equals(isLocked)){
                return true;
            } else {
                Thread.sleep(20);
            }
        }
        return false;
    }
    public Boolean unlock(String key){
        System.out.println("unlock:"+key);
        return redisTemplate
                .delete(key);
    }
}
