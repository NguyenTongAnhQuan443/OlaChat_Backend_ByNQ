package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshTokens:";

    // Truy cập Redis Hash operations
    private HashOperations<String, String, String> getHashOps() {
        return redisTemplate.opsForHash();
    }

    public String createRefreshToken(UUID userId, String deviceId) {
        String token = UUID.randomUUID().toString();
        long expiryDuration = 1000 * 60 * 60 * 24 * 7; // 7 ngày

        // Xóa token cũ nếu có trước khi tạo mới
        deleteRefreshToken(userId.toString(), deviceId);

        // Lưu token mới vào Redis Hash
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        getHashOps().put(key, userId.toString(), token);
        redisTemplate.expire(key, Duration.ofMillis(expiryDuration));

        return token;
    }

    public Optional<String> findByUserAndDevice(UUID userId, String deviceId) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        return Optional.ofNullable(getHashOps().get(key, userId.toString()));
    }

    public Optional<String> findByTokenAndDevice(String token, String deviceId) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;

        for (var entry : getHashOps().entries(key).entrySet()) {
            if (entry.getValue().equals(token)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Optional<String> findUserIdByDeviceAndToken(String deviceId, String token) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;

        for (var entry : getHashOps().entries(key).entrySet()) {
            if (entry.getValue().equals(token)) {
                return Optional.of(entry.getKey()); // Trả về userId
            }
        }
        return Optional.empty();
    }

    public void deleteRefreshToken(String userId, String deviceId) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        getHashOps().delete(key, userId);
    }

    public void deleteByUserAndDevice(String userId, String deviceId) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        getHashOps().delete(key, userId);
    }

}
