package com.example.springdataredis.redisEx1;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login/hash")
    public String CreateSessionTti(@RequestParam String userId){
        // 세션 생성(tti + ttl)
        return sessionService.createSessionByHash(userId,"테스트");
    }

    @PostMapping("/login/string")
    public String CreateSessionTtl(@RequestParam String userId){
        // 세션 생성(ttl)
        return sessionService.createSessionByString(userId);
    }

    @GetMapping("/login/hash")
    public Map<String,Object> getSessionByHash(@RequestParam String sessionId){
        // 세션 생성(tti + ttl)
        return sessionService.getSessionByHash(sessionId);
    }

    @GetMapping("/login/string")
    public String getSessionByString(@RequestParam String sessionId){
        // 세션 생성(ttl)
        return sessionService.getSessionByString(sessionId);
    }

    @GetMapping("/logout")
    public void deleteSession(){
        // 세션 삭제
    }
}
