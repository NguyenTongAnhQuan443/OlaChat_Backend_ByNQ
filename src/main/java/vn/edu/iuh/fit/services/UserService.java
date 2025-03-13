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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TwilioService twilioService;

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại!"));
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

    public Optional<User> getUserByPhonenumber(String phonenumber) {
        return userRepository.findUserByPhoneNumber(phonenumber);
    }

    public void sendOtpToUser(String phoneNumber) {
        Optional<User> existingUser = userRepository.findUserByPhoneNumber(phoneNumber);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }
        twilioService.sendOtp(phoneNumber);
    }

    public User registerUserWithOtp(RegisterUserDTO registerUserDTO, String otp) {
        if (!twilioService.verifyOtp(registerUserDTO.getPhoneNumber(), otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Xóa OTP sau khi xác thực thành công
        twilioService.removeOtp(registerUserDTO.getPhoneNumber());

        User newUser = userMapper.toUser(registerUserDTO);
        return userRepository.save(newUser);
    }
}
