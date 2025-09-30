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
        // 세션 생성(tti)
    }

    @PostMapping("/login/ttl")
    public void CreateSessionTtl(){
        // 세션 생성(ttl)
    }

    @GetMapping("/session")
    public void getSession(){
        // 조회
    }

    @GetMapping("/logout")
    public void deleteSession(){
        // 세션 삭제
    }
}
