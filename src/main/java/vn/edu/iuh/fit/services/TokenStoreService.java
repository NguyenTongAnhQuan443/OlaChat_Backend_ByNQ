package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenStoreService {
    private final StringRedisTemplate redisTemplate;
    private static final String ACCESS_PREFIX = "accessToken:";
    private static final String REFRESH_PREFIX = "refreshToken:";

    public void storeAccessToken(UUID userId, String deviceId, String token) {
        String key = ACCESS_PREFIX + userId + ":" + deviceId;
        redisTemplate.opsForValue().set(key, token, Duration.ofHours(10));
    }

    public void storeRefreshToken(UUID userId, String deviceId, String token) {
        String key = REFRESH_PREFIX + userId + ":" + deviceId;
        redisTemplate.opsForValue().set(key, token, Duration.ofDays(10));
    }

    public Optional<String> getRefreshToken(UUID userId, String deviceId) {
        String key = REFRESH_PREFIX + userId + ":" + deviceId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void removeTokens(UUID userId, String deviceId) {
        redisTemplate.delete(ACCESS_PREFIX + userId + ":" + deviceId);
        redisTemplate.delete(REFRESH_PREFIX + userId + ":" + deviceId);
    }
}