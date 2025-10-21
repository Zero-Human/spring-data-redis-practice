package com.example.springdataredis;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class RedisTemplateTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void redisTemplateString() {
//        SET login:count "1" EX 600  # login:count 1 600초 TTL
//        INCR login:count            # 1 증가
//        GET login:count             # login:count 조회
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        valueOperations.set("login:count", "1", Duration.ofSeconds(10));
        valueOperations.increment("login:count");
        String count = valueOperations.get("login:count");
        valueOperations.getAndExpire("user:1001",Duration.ofMinutes(30));
        System.out.println(redisTemplate.getExpire("login:count"));


        Assertions.assertEquals(count, "2");
    }
    @Test
    void redisTemplateList() {
//        RPUSH queue:tasks "task1" 오른쪽 queue:tasks에 "task1" 삽입
//        RPUSH queue:tasks "task2" 오른쪽 queue:tasks에 "task2" 삽입
//        LPOP queue:tasks 왼쪽 리스트 꺼내기
//        LPOP queue:tasks 왼쪽 리스트 꺼내기
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

//        HSET user:1001 name "Alice"
//        HSET user:1001 age "30"
//        HGET user:1001 name             # "Alice"
//        HGETALL user:1001               # 모든 필드 조회
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
    @Test
    void redisTemplateSet() {
//        SADD team:dev "alice" "bob"
//        SMEMBERS team:dev               # ["alice","bob"]
//        SINTER team:dev team:test       # 교집합
        SetOperations<String, String> ops = redisTemplate.opsForSet();

        ops.add("team:dev", "alice", "bob");

        Set<String> members = ops.members("team:dev");
        Set<String> common = ops.intersect("team:dev", "team:test");

        Assertions.assertEquals(members, Set.of("alice","bob"));
        Assertions.assertEquals(common, Set.of());
    }
    @Test
    void redisTemplateZSet() {
//        ZADD ranking 100 "alice"
//        ZADD ranking 80 "bob"
//        ZINCRBY ranking 20 "alice"
//        ZREVRANGE ranking 0 1 WITHSCORES  # 상위 2명
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();

        ops.add("ranking", "alice", 100);
        ops.add("ranking", "bob", 80);

        ops.incrementScore("ranking", "alice", 20);

        Set<String> top = ops.reverseRange("ranking", 0, 1);

        Assertions.assertEquals(top, Set.of("alice","bob"));
    }
    @Test
    void redisTemplateBitmap() {
//        SETBIT attendance:20250922 1001 1
//        GETBIT attendance:20250922 1001  # 1 (true)
//        BITCOUNT attendance:20250922     # 출석자 수
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // 1001번 유저 출석 체크
        ops.setBit("attendance:20250922", 1001, true);

        // 조회
        Boolean present = ops.getBit("attendance:20250922", 1001);

        Assertions.assertEquals(present, true);
    }
    @Test
    void redisTemplateHyperLogLog() {
//        PFADD uv:20250922 "user1"
//        PFADD uv:20250922 "user2"
//        PFCOUNT uv:20250922             # 고유 사용자 수
        HyperLogLogOperations<String, String> ops = redisTemplate.opsForHyperLogLog();

        ops.add("uv:20250922", "user1");
        ops.add("uv:20250922", "user2");
        Long uv = ops.size("uv:20250922");

        Assertions.assertEquals(uv, 2);
    }
}
