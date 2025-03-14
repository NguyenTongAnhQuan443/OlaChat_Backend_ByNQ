package vn.edu.iuh.fit.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.constants.CodeConstants;
import jakarta.servlet.http.Cookie;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.utils.JwtUtil;
import org.springframework.http.ResponseCookie;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String GOOGLE_CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(String phoneNumber, String password, String deviceId, HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findUserByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_LOGIN_FAILED, null));
        }
        return buildAuthResponse(userOpt.get(), deviceId, response);
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithOAuth(String provider, String idToken, String deviceId, HttpServletResponse response) {
        User user = null;
        System.out.println("Received idToken: " + idToken); // In ra idToken
        switch (provider.toUpperCase()) {
            case "GOOGLE":
                user = authenticateGoogle(idToken);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(CodeConstants.CODE_BAD_REQUEST, "Phương thức OAuth không hợp lệ!", null));
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, "OAuth token không hợp lệ!", null));
        }

        return buildAuthResponse(user, deviceId, response);
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(
            HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy Refresh Token từ Cookie
            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("refreshToken"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, "Refresh token không tồn tại!", null));
            }

            // Giải mã Refresh Token
            String userId = jwtUtil.extractUserId(refreshToken, true).toString();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, "Refresh token không hợp lệ!", null));
            }

            // Kiểm tra refreshToken có trong Redis không
            Optional<String> storedToken = refreshTokenService.findByTokenAndDevice(refreshToken, request.getHeader("deviceId"));
            if (storedToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, "Refresh token không tồn tại!", null));
            }

            // Tạo Access Token mới
            String newAccessToken = jwtUtil.generateAccessToken(UUID.fromString(userId));

            // Tạo Refresh Token mới
            String newRefreshToken = refreshTokenService.createRefreshToken(UUID.fromString(userId), request.getHeader("deviceId"));

            // Lưu Refresh Token mới vào Cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(10 * 24 * 60 * 60)
                    .build();

            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, "Làm mới Access Token thành công!",
                    Map.of("accessToken", newAccessToken)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(CodeConstants.CODE_SERVER_ERROR, "Lỗi hệ thống!" + e.getMessage(), null));
        }
    }


    public ResponseEntity<ApiResponse<String>> logout(String accessToken, String deviceId) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        UUID userId;
        try {
            userId = jwtUtil.extractUserId(accessToken, false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_INVALID_ACCESS_TOKEN, null));
        }

        // Kiểm tra xem user có refreshToken trên deviceId này không
        Optional<String> tokenOptional = refreshTokenService.findByUserAndDevice(userId, deviceId);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(CodeConstants.CODE_NOT_FOUND, AuthConstants.MESSAGE_DEVICE_LOGOUT_NOT_FOUND, null));
        }

        // Xóa refresh token khỏi Redis
        refreshTokenService.deleteByUserAndDevice(userId.toString(), deviceId);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGOUT_SUCCESS, null));
    }

    private User authenticateGoogle(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                System.out.println("Lỗi: Token Google không hợp lệ hoặc không thể xác minh!");
                return null;
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            System.out.println("Decoded Google ID Token: " + payload);

            return userService.findOrCreateUser(
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture"),
                    AuthProvider.GOOGLE
            );
        } catch (Exception e) {
            return null;
        }
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> buildAuthResponse(User user, String deviceId, HttpServletResponse response) {
        UserDTO userDTO = userMapper.toUserDTO(user);
        // Kiểm tra Access Token còn hạn trong Redis
        Optional<String> existingAccessToken = jwtUtil.findExistingAccessToken(user.getId());
        String accessToken = existingAccessToken.orElseGet(() -> jwtUtil.generateAccessToken(user.getId()));

        // Nếu Refresh Token còn hạn, sử dụng lại thay vì tạo mới
        Optional<String> existingRefreshToken = refreshTokenService.findByUserAndDevice(user.getId(), deviceId);
        String refreshToken = existingRefreshToken.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId(), deviceId));
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, "Đăng nhập thành công!",
                Map.of("accessToken", accessToken, "user", userDTO)
        ));
    }

}
