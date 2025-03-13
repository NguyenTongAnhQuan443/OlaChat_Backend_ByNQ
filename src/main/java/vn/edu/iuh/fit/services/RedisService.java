package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String RESET_OTP_PREFIX = "reset_otp:";
    private static final String RESET_ATTEMPT_PREFIX = "reset_attempt:";

    // Lưu OTP vào Redis - hết hạn sau 5 phút
    public void saveOtp(String email, String otp) {
        redisTemplate.opsForValue().set(RESET_OTP_PREFIX + email, otp, 5, TimeUnit.MINUTES);
    }

    // Lấy OTP từ Redis
    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(RESET_OTP_PREFIX + email);
    }

    // Xóa OTP sau khi sử dụng
    public void deleteOtp(String email) {
        redisTemplate.delete(RESET_OTP_PREFIX + email);
    }

    // Kiểm yêu cầu OTP - 1 lần mỗi giờ
    public boolean isAllowedToRequestReset(String email) {
        return redisTemplate.opsForValue().get(RESET_ATTEMPT_PREFIX + email) == null;
    }

    // Lấy thời gian còn lại để có thể gửi yêu cầu mới
    public long getTimeUntilNextRequest(String email) {
        String lastRequestTime = redisTemplate.opsForValue().get(RESET_ATTEMPT_PREFIX + email);
        if (lastRequestTime == null) {
            return 0; // Có thể gửi ngay
        }

        long lastRequestMillis = Long.parseLong(lastRequestTime);
        long elapsedTime = System.currentTimeMillis() - lastRequestMillis;
        long remainingTime = 3600000 - elapsedTime; // Thời gian còn lại

        return Math.max(remainingTime, 0);
    }

    // Đánh dấu yêu cầu OTP để tránh spam - hết hạn sau 1 giờ
    public void setResetAttemptLimit(String email) {
        redisTemplate.opsForValue().set(RESET_ATTEMPT_PREFIX + email, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.HOURS);
    }
}
