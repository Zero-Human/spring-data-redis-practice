package com.example.springdataredis.redisEx1;

import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private static final String SESSION_KEY_PREFIX = "session:user:";
}
