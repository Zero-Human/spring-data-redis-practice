package com.example.springdataredis.redisEx1;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/login/tti")
    public void CreateSessionTti(){
        // 저장
    }

    @PostMapping("/login/ttl")
    public void CreateSessionTtl(){
        // 저장
    }

    @GetMapping("/session")
    public void getSession(){
        // 저장
    }

    @GetMapping("/logout")
    public void deleteSession(){
        // 세션 삭제
    }
}
