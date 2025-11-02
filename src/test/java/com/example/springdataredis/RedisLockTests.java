package com.example.springdataredis;

import com.example.springdataredis.redisEx2.RedisLockRepository;
import com.example.springdataredis.redisEx2.RedissonLockRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisLockTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisLockRepository redisLockRepository;
    @Autowired
    private RedissonLockRepository redissonLockRepository;

    public static Long count = 0L;

    @BeforeEach
    public void before(){
        count = 0L;
    }

    public static void increase() throws InterruptedException {
        count ++;
        Thread.sleep(10);
    }
    // 동시성 문제가 발생하는 코드
    @Test
    public void 동시카운드_100() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    try {
                        increase();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 100 - (100 * 1) = 0
        assertEquals(count, 100L);
    }
    // Lettuce로 spin Lock 방식
    @Test
    public void 동시카운드_100_redisLock() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String key = "key";
            executorService.submit(() -> {
                try {
                    try {
                        // 획득할 때까지 반복한다.
                        Boolean isLocked = redisLockRepository.lock(key);
                        if (Boolean.TRUE.equals(isLocked)) {
                            // 수행할 로직
                            increase();
                            redisLockRepository.unlock(key);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        // 100 - (100 * 1) = 0
        assertEquals(count, 100L);
    }
    // redisson 방식
    @Test
    public void 동시카운드_100_redissonLock() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String key = "key";
            executorService.submit(() -> {
                try {
                    try {
                        redissonLockRepository.lock(key);
                        increase();
                        redissonLockRepository.unlock(key);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        // 100 - (100 * 1) = 0
        assertEquals(count, 100L);
    }
}
