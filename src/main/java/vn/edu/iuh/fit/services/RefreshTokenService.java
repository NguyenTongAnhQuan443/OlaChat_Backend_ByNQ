package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.utils.JwtUtil;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private static final String REFRESH_TOKEN_PREFIX = "refreshTokens:";

    // Truy cập Redis Hash operations
    private HashOperations<String, String, String> getHashOps() {
        return redisTemplate.opsForHash();
    }

    public String createRefreshToken(UUID userId, String deviceId) {
        String token = jwtUtil.generateRefreshToken(userId);

        // Xóa token cũ nếu có trước khi tạo mới
        deleteRefreshToken(userId.toString(), deviceId);

        // Lưu token mới vào Redis Hash
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        getHashOps().put(key, userId.toString(), token);

        return token;
    }

    public Optional<String> findByUserAndDevice(UUID userId, String deviceId) {
        String key = REFRESH_TOKEN_PREFIX + deviceId;
        return Optional.ofNullable(getHashOps().get(key, userId.toString()));
    }

    public Optional<String> findByTokenAndDevice(String token, String deviceId) {
        try {
            String userId = jwtUtil.extractUserId(token, true).toString();
            String key = REFRESH_TOKEN_PREFIX + deviceId;

            for (var entry : getHashOps().entries(key).entrySet()) {
                if (entry.getValue().equals(token)) {
                    return Optional.of(entry.getValue());
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty(); // Token không hợp lệ
        }
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
