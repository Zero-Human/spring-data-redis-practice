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
            // 락 획득을 시도한다(10초동안 시도를 할 예정이며 획득할 경우 1초안에 해제할 예정이다)
            Boolean isLocked = lock.tryLock(10,1, TimeUnit.SECONDS);
            System.out.println("lock" + key +"::"+isLocked);
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
        System.out.println("unlock" + key);
        return true;
    }
}
