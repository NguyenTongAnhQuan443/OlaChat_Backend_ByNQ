package vn.edu.iuh.fit.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private final Map<String, String> otpStorage = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        otpStorage.put(phoneNumber, otp); // Lưu OTP vào HashMap tạm thời

        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "OlaChat Social - Mã OTP của bạn là: " + otp
        ).create();

        System.out.println("OTP đã gửi: " + message.getSid());
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        return otpStorage.containsKey(phoneNumber) && otpStorage.get(phoneNumber).equals(otp);
    }

    public void removeOtp(String phoneNumber) {
        otpStorage.remove(phoneNumber); // Xóa OTP sau khi xác thực thành công
    }
}
