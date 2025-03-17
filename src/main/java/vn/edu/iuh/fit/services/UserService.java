package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.constants.CodeConstants;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.enums.Role;
import vn.edu.iuh.fit.enums.UserStatus;
import vn.edu.iuh.fit.exceptions.CustomException;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.utils.FormatPhoneNumber;

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
    private final OtpStorageService otpStorageService;


    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
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
                    .authProvider(AuthProvider.LOCAL)
                    .role(Role.USER)
                    .status(UserStatus.ACTIVE)
                    .build();
            return userRepository.save(newUser);
        });
    }

    public void sendPasswordResetOtp(String email) {
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isEmpty()) {
            throw new CustomException(CodeConstants.CODE_NOT_FOUND, "Email không tồn tại trong hệ thống!", null);
        }

        if (!redisService.isAllowedToRequestReset(email)) {
            long waitTimeMillis = redisService.getTimeUntilNextRequest(email);
            long waitMinutes = (waitTimeMillis / 60000);
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST,
                    "Bạn đã yêu cầu đặt lại mật khẩu gần đây. Vui lòng thử lại sau " + waitMinutes + " phút.", null);
        }

        String otp = generateOtp();
        redisService.saveOtp(email, otp);
        emailService.sendOtpEmail(email, otp);
        redisService.setResetAttemptLimit(email);
    }

    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        String storedOtp = redisService.getOtp(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST, "Mã OTP không hợp lệ hoặc đã hết hạn!", null);
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new CustomException(CodeConstants.CODE_NOT_FOUND, "Email không tồn tại!", null));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisService.deleteOtp(email);
    }

    public void checkPhoneAndSendOtp(RegisterUserDTO registerUserDTO) {
        Optional<User> existingUser = userRepository.findUserByPhoneNumber(registerUserDTO.getPhoneNumber());
        if (existingUser.isPresent()) {
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST, "Số điện thoại đã được sử dụng!", null);
        }

        String otp = generateOtp();
        otpStorageService.saveOtp(registerUserDTO.getPhoneNumber(), otp, registerUserDTO); // Lưu cả thông tin user
        twilioService.sendOtp(registerUserDTO.getPhoneNumber(), otp);
    }

    public User verifyOtpAndRegisterUser(String phoneNumber, String otp) {
        if (!otpStorageService.verifyOtp(phoneNumber, otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Lấy thông tin đăng ký từ bộ nhớ tạm
        RegisterUserDTO registerUserDTO = otpStorageService.getUserData(phoneNumber);
        if (registerUserDTO == null) {
            throw new CustomException(CodeConstants.CODE_NOT_FOUND, "Không tìm thấy dữ liệu đăng ký!", null);
        }

        String hashedPassword = passwordEncoder.encode(registerUserDTO.getPassword());
        String formattedPhoneNumber = FormatPhoneNumber.formatPhoneNumberTo0(registerUserDTO.getPhoneNumber());

        // Tạo user từ dữ liệu tạm
        User newUser = User.builder()
                .phoneNumber(formattedPhoneNumber)
                .displayName(registerUserDTO.getDisplayName())
                .password(hashedPassword)
                .email(registerUserDTO.getEmail())
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .build();

        userRepository.save(newUser);
        otpStorageService.removeOtp(phoneNumber); // Xóa dữ liệu tạm

        return newUser;
    }

    private String generateOtp() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}
