package com.example.springdataredis.redisEx1;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {
    private final String SESSION_KEY_PREFIX = "session:user:";
    // Redis에 저장될 세션 데이터의 만료 시간 설정
    private final Duration ABSOLUTE_TTL = Duration.ofHours(1); // 1시간 (TTL)
    private final Duration IDLE_TIMEOUT = Duration.ofMinutes(5);  // 5분 (TTI)
    private final Long ONE_HOUR = 60 * 60 * 1000L;

    private final RedisTemplate<String, Object> redisTemplate;

    public SessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createSessionByHash(String userId, String userName){
        // Hash로 구현(TTI + TTL 사용)
        HashOperations<String,String, Object> hashOperations = redisTemplate.opsForHash();
        String sessionId = SESSION_KEY_PREFIX.concat(UUID.randomUUID().toString());

        Map<String, Object> userInfo = Map.of("userId",userId,
                                            "userName",userName,
                                            "loginTime",System.currentTimeMillis());
        hashOperations.putAll(sessionId,userInfo);

        // TTI로 설정
        redisTemplate.expire(sessionId, IDLE_TIMEOUT);

        return sessionId;
    }
    public Map<String, Object> getSessionByHash(String sessionId){
        // Ttl이 만료되기 전에 tti가 유효한지 확인
        // Ttl 만료되면 null 반환 ttl 만료 전이면 tti 갱신
        HashOperations<String,String, Object> hashOperations = redisTemplate.opsForHash();
        Map<String, Object> userInfo = hashOperations.entries(sessionId);
        // TTI 만료 확인
        if(userInfo.isEmpty()){
            return null;
        }
        // TTL 만료 확인
        String loginTime = userInfo.get("loginTime").toString();
        if(System.currentTimeMillis() - Long.valueOf(loginTime) >  ONE_HOUR){
            return null;
        }
        // TTI 갱신
        redisTemplate.expire(sessionId, IDLE_TIMEOUT);
        return userInfo;
    }

    public String createSessionByString(String userId){
        // Ttl 방식을 String으로 구현
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String sessionId = SESSION_KEY_PREFIX.concat(UUID.randomUUID().toString());
        // TTL (Time To Live) 설정: 1시간 후 무조건 만료
        valueOperations.set(sessionId, userId, ABSOLUTE_TTL);
        return sessionId;
    }
    public String getSessionByString(String sessionId){
        // Ttl이 만료확인
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String userId = valueOperations.get(sessionId).toString();

        return userId;
    }

}
