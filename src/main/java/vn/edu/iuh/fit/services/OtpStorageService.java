package vn.edu.iuh.fit.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpStorageService {
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, RegisterUserDTO> userTempStorage = new ConcurrentHashMap<>();

    private static final long OTP_EXPIRATION_TIME = 10 * 60 * 1000; // 10 phút

    private static class OtpEntry {
        String otp;
        long expirationTime;

        OtpEntry(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }
    }

    public void saveOtp(String phoneNumber, String otp, RegisterUserDTO registerUserDTO) {
        otpStorage.put(phoneNumber, new OtpEntry(otp, System.currentTimeMillis() + OTP_EXPIRATION_TIME));
        userTempStorage.put(phoneNumber, registerUserDTO);
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpEntry otpEntry = otpStorage.get(phoneNumber);
        if (otpEntry == null || System.currentTimeMillis() > otpEntry.expirationTime) {
            otpStorage.remove(phoneNumber);
            userTempStorage.remove(phoneNumber);
            return false;
        }
        return otpEntry.otp.equals(otp);
    }

    public RegisterUserDTO getUserData(String phoneNumber) {
        return userTempStorage.get(phoneNumber);
    }

    public void removeOtp(String phoneNumber) {
        otpStorage.remove(phoneNumber);
        userTempStorage.remove(phoneNumber);
    }
}
