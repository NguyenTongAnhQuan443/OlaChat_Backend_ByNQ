package vn.edu.iuh.fit.services.Authentication;

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
import jakarta.servlet.http.HttpServletRequest;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.services.RefreshTokenService;
import vn.edu.iuh.fit.services.TokenBlacklistService;
import vn.edu.iuh.fit.services.interfaces.IOAuthProvider;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.utils.JwtUtil;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final OAuthProviderFactory oAuthServiceFactory;

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithOAuth(String provider, String idToken, String deviceId, HttpServletResponse response) {
        try {
            IOAuthProvider oauthService = oAuthServiceFactory.getOAuthService(provider);
            String accessToken = oauthService.verifyToken(idToken);
            User user = oauthService.getUserFromToken(idToken);

            Optional<String> existingRefreshToken = refreshTokenService.findByUserAndDevice(user.getId(), deviceId);
            String refreshToken = existingRefreshToken.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId(), deviceId));

            // Đặt Refresh Token vào HTTP-only Cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)  // Ngăn JavaScript truy cập
                    .secure(true)    // Chỉ gửi qua HTTPS
                    .path("/")       // Có thể sử dụng trên toàn bộ domain
                    .maxAge(10 * 24 * 60 * 60) // 10 ngày
                    .build();

            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of("accessToken", accessToken)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, "OAuth token không hợp lệ!", null));
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(String phoneNumber, String password, String deviceId, HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findUserByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_LOGIN_FAILED, null));
        }

        User user = userOpt.get();
        String accessToken = jwtUtil.generateAccessToken(user.getId());

        Optional<String> existingRefreshToken = refreshTokenService.findByUserAndDevice(user.getId(), deviceId);
        String refreshToken = existingRefreshToken.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId(), deviceId));

        // Đặt Refresh Token vào HTTP-only Cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)  // Ngăn JavaScript truy cập
                .secure(true)    // Chỉ gửi qua HTTPS
                .path("/")       // Có thể sử dụng trên toàn bộ domain
                .maxAge(10 * 24 * 60 * 60) // 10 ngày
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of("accessToken", accessToken)));
    }

    //    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(String refreshToken, String deviceId, HttpServletResponse response) {
//        try {
//            // Kiểm tra refreshToken có hợp lệ không
//            String userId = jwtUtil.extractUserId(refreshToken, true).toString(); // Giải mã token
//            if (userId == null) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, "Refresh token không hợp lệ!", null));
//            }
//
//            // Kiểm tra refreshToken có trong Redis không
//            Optional<String> storedToken = refreshTokenService.findByTokenAndDevice(refreshToken, deviceId);
//            if (storedToken.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, "Refresh token không tồn tại!", null));
//            }
//
//            // Tạo Access Token mới
//            String newAccessToken = jwtUtil.generateAccessToken(UUID.fromString(userId));
//
//            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, "Làm mới Access Token thành công!", Map.of(
//                    "accessToken", newAccessToken
//            )));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(CodeConstants.CODE_SERVER_ERROR, "Lỗi hệ thống!" + e.getMessage(), null));
//        }
//    }
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

        // Thêm accessToken vào blacklist để chặn sử dụng lại
        tokenBlacklistService.addToBlacklist(accessToken);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGOUT_SUCCESS, null));
    }

}
