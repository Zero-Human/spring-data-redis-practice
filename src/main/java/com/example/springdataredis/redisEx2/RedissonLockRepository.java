package com.example.springdataredis.redisEx2;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


// pub-sub 구조의 lock 방식
@Component
public class RedissonLockRepository {
    private RedissonClient redissonClient;

    public RedissonLockRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public Boolean lock(String key){
        RLock lock = redissonClient.getLock(key);
        try {
            Boolean isLocked = lock.tryLock(10,1, TimeUnit.SECONDS);
            return isLocked;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public Boolean unlock(String key){
        RLock lock = redissonClient.getLock(key);
        if(lock.isLocked()){
            lock.unlock();
        }
        return true;

    }
}
