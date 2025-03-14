package vn.edu.iuh.fit.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("OlaChat Social");
            helper.setText(
                    "<p>OlaChat Social - Xin chào.</p>" +
                            "<p>Bạn đã yêu cầu đặt lại mật khẩu. Đây là mã OTP của bạn:</p>" +
                            "<h2 style='color:blue;'>" + otp + "</h2>" +
                            "<p>OTP này có hiệu lực trong 5 phút.</p>" +
                            "<p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>" +
                            "<p>OlaChat Team</p>" +
                            "<p>Trân trọng.</p>",
                    true
            );
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
