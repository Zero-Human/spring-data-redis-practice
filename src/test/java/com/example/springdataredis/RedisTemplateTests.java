package com.example.springdataredis;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;

@SpringBootTest
public class RedisTemplateTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void redisTemplateString() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = "name";
        valueOperations.set(key, "giraffe");
        String value = valueOperations.get(key);
        Assertions.assertEquals(value, "giraffe");
    }
    @Test
    void redisTemplateList() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        // 오른쪽 삽입
        listOperations.rightPush("queue:tasks", "task1");
        listOperations.rightPush("queue:tasks", "task2");

        // 왼쪽에서 꺼내기 (FIFO 큐)
        String task1 = listOperations.rightPop("queue:tasks"); // "task1"
        String task2 = listOperations.rightPop("queue:tasks"); // "task1"

        Assertions.assertEquals(task1, "task2");
        Assertions.assertEquals(task2, "task1");
    }
    @Test
    void redisTemplateHash() {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // 저장
        hashOperations.put("user:1001", "name", "Alice");
        hashOperations.put("user:1001", "age", "30");

        // 조회
        String name = hashOperations.get("user:1001", "name");
        Map<String, String> user = hashOperations.entries("user:1001");

        Assertions.assertEquals(name, "Alice");
        Assertions.assertEquals(user, Map.of("name","Alice","age","30"));
    }
}
