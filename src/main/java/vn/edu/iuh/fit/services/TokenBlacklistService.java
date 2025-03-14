package vn.edu.iuh.fit.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklisted:";

//    @PostConstruct
//    public void checkRedisConnection() {
//        try {
//            redisTemplate.opsForValue().set("test_connection", "OK");
//            String result = redisTemplate.opsForValue().get("test_connection");
//            System.out.println("Redis Connection Test: " + result);
//        } catch (Exception e) {
//            System.err.println("Không thể kết nối đến Redis: " + e.getMessage());
//        }
//    }

    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", Duration.ofHours(10));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
