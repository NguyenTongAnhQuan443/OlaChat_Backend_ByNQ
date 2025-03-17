package vn.edu.iuh.fit.services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    private static final long OTP_EXPIRATION_TIME = 3 * 60 * 1000; // 10 phút

    private static class OtpEntry {
        String otp;
        long expirationTime;

        OtpEntry(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }
    }

    public void sendOtp(String phoneNumber, String otp) {
        long expirationTime = System.currentTimeMillis() + OTP_EXPIRATION_TIME;
        otpStorage.put(phoneNumber, new OtpEntry(otp, expirationTime));

        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "OlaChat Social - Mã OTP của bạn là: " + otp + " (có hiệu lực trong 10 phút)"
        ).create();
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpEntry otpEntry = otpStorage.get(phoneNumber);
        if (otpEntry == null) {
            return false;
        }

        if (System.currentTimeMillis() > otpEntry.expirationTime) {
            otpStorage.remove(phoneNumber); // Xóa OTP nếu đã hết hạn
            return false;
        }

        return otpEntry.otp.equals(otp);
    }

    public void removeOtp(String phoneNumber) {
        otpStorage.remove(phoneNumber);
    }
}
