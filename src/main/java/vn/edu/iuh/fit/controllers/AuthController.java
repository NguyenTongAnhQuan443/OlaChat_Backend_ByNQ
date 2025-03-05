package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.utils.JwtUtil;

import java.util.Map;

@Tag(name = "Auth API", description = "Quản lý xác thực và phân quyền")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

//    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String password = request.get("password");

        User user = userService.getUserByPhonenumber(phoneNumber);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác !");
        }

        String token = jwtUtil.generateToken(phoneNumber);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
