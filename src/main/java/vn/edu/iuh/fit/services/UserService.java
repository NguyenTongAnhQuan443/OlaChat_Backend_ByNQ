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

    public User registerUser(RegisterUserDTO registerUserDTO) {
        Optional<User> existingUser = userRepository.findUserByPhoneNumber(registerUserDTO.getPhoneNumber());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }

        User newUser = userMapper.toUser(registerUserDTO);
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        return userRepository.save(newUser);
    }
}
