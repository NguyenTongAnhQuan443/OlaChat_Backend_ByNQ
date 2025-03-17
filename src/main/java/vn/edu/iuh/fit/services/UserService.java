package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.enums.Role;
import vn.edu.iuh.fit.enums.UserStatus;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TwilioService twilioService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, RegisterUserDTO> tempUserStorage = new ConcurrentHashMap<>();

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại!"));
    }

    public Optional<User> getUserByPhonenumber(String phonenumber) {
        return userRepository.findUserByPhoneNumber(phonenumber);
    }

    public User findOrCreateUser(String email, String displayName, String avatar, AuthProvider provider) {
        return userRepository.findUserByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .displayName(displayName)
                    .avatar(avatar)
                    .authProvider(provider)
                    .role(Role.USER)
                    .status(UserStatus.ACTIVE)
                    .build();
            return userRepository.save(newUser);
        });
    }

    public void sendPasswordResetOtp(String email) {
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại trong hệ thống!");
        }

        if (!redisService.isAllowedToRequestReset(email)) {
            long waitTimeMillis = redisService.getTimeUntilNextRequest(email);
            long waitMinutes = (waitTimeMillis / 60000);
            throw new RuntimeException("Bạn đã yêu cầu đặt lại mật khẩu gần đây. Vui lòng thử lại sau " + waitMinutes + " phút.");
        }

        String otp = generateOtp();
        redisService.saveOtp(email, otp);
        emailService.sendOtpEmail(email, otp);
        redisService.setResetAttemptLimit(email);
    }

    // Xác thực OTP và đặt lại mật khẩu
    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        String storedOtp = redisService.getOtp(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng
        redisService.deleteOtp(email);
    }

    public void checkPhoneAndSendOtp(RegisterUserDTO registerUserDTO) {
        Optional<User> existingUser = userRepository.findUserByPhoneNumber(registerUserDTO.getPhoneNumber());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }

        // Lưu thông tin user vào bộ nhớ tạm để sử dụng sau khi OTP hợp lệ
        tempUserStorage.put(registerUserDTO.getPhoneNumber(), registerUserDTO);

        // Gửi OTP
        String otp = generateOtp();
        twilioService.sendOtp(registerUserDTO.getPhoneNumber(), otp);
    }

    public User verifyOtpAndRegisterUser(String phoneNumber, String otp) {
        // Kiểm tra OTP
        if (!twilioService.verifyOtp(phoneNumber, otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Lấy lại thông tin đăng ký cũ từ bộ nhớ tạm
        RegisterUserDTO registerUserDTO = tempUserStorage.get(phoneNumber);
        if (registerUserDTO == null) {
            throw new RuntimeException("Không tìm thấy dữ liệu đăng ký!");
        }

        // Hash mật khẩu trước khi lưu
        String hashedPassword = passwordEncoder.encode(registerUserDTO.getPassword());

        // Tạo tài khoản mới
        User newUser = User.builder()
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .displayName(registerUserDTO.getDisplayName())
                .password(hashedPassword)
                .email(registerUserDTO.getEmail())
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .build();

        // Lưu vào database
        userRepository.save(newUser);

        // Xóa dữ liệu tạm
        tempUserStorage.remove(phoneNumber);
        twilioService.removeOtp(phoneNumber);

        return newUser;
    }

    private String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}
