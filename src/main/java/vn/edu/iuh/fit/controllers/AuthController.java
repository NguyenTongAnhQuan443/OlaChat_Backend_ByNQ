package vn.edu.iuh.fit.controllers;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.constants.CodeConstants;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.RefreshToken;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.services.*;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.utils.JwtUtil;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Auth API", description = "Quản lý xác thực và phân quyền")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final GoogleAuthService googleAuthService;
    private final FacebookAuthService facebookAuthService;

    @PostMapping("/login-facebook")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithFacebook(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        try {
            String token = facebookAuthService.verifyFacebookToken(accessToken);
            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of(
                    "accessToken", token
            )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, "Facebook token không hợp lệ", null));
        }
    }


    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        try {
            String accessToken = googleAuthService.verifyGoogleToken(idToken);

            // In ra accessToken để kiểm tra
            System.out.println("Generated Access Token: " + accessToken);

            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of(
                    "accessToken", accessToken
            )));
        } catch (Exception e) {
            System.err.println("Google token không hợp lệ: " + e.getMessage()); // In lỗi nếu có
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, "Google token không hợp lệ", null));
        }
    }


    @PostMapping("/login-phone")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String password = request.get("password");

        // Kiểm tra thông tin đăng nhập
        Optional<User> userOpt = userRepository.findUserByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_LOGIN_FAILED, null));
        }

        User user = userOpt.get();
        String accessToken = jwtUtil.generateToken(user.getId());
        UserDTO userDTO = userMapper.toUserDTO(user);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of(
                "accessToken", accessToken,
                "user", userDTO
        )));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(CodeConstants.CODE_BAD_REQUEST, AuthConstants.MESSAGE_REFRESH_TOKEN_REQUIRED, null));
        }

        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_INVALID, null));
        }

        RefreshToken token = tokenOptional.get();

        if (token.getExpiryDate().before(new Date())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_EXPIRED, null));
        }

        String newAccessToken = jwtUtil.generateToken(token.getUser().getId());

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_REFRESH_TOKEN_SUCCESS, Map.of(
                "accessToken", newAccessToken
        )));
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(CodeConstants.CODE_BAD_REQUEST, AuthConstants.MESSAGE_ACCESS_TOKEN_REQUIRED, null));
        }

        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        UUID userId;
        try {
            userId = jwtUtil.extractUserId(accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_INVALID_ACCESS_TOKEN, null));
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(CodeConstants.CODE_NOT_FOUND, AuthConstants.MESSAGE_USER_NOT_FOUND, null));
        }
        // Xóa refresh token
        refreshTokenService.deleteByUser(userOpt.get());

        // Thêm Access Token vào Blacklist
        tokenBlacklistService.addToBlacklist(accessToken);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGOUT_SUCCESS, null));
    }


}