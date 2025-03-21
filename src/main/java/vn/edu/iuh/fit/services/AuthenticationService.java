// === AuthenticationService.java ===
package vn.edu.iuh.fit.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.constants.CodeConstants;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.security.JwtProvider;
import vn.edu.iuh.fit.utils.ApiResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenStoreService tokenStoreService;
    private final OAuthService oAuthService;
    private final UserMapper userMapper;

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(
            String phoneNumber, String password, String deviceId, HttpServletResponse response) {

        Optional<User> userOpt = userRepository.findUserByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_LOGIN_FAILED, null));
        }

        return buildAuthResponse(userOpt.get(), deviceId, response);
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithOAuth(
            String provider, String idToken, String deviceId, HttpServletResponse response) {

        if (!"GOOGLE".equalsIgnoreCase(provider)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(CodeConstants.CODE_BAD_REQUEST, AuthConstants.MESSAGE_OAUTH_PROVIDER_INVALID, null));
        }

        User user = oAuthService.authenticateWithGoogle(idToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_OAUTH_TOKEN_INVALID, null));
        }

        return buildAuthResponse(user, deviceId, response);
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(
            HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_NOT_FOUND, null));
        }

        String refreshToken = null;
        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                refreshToken = c.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_NOT_FOUND, null));
        }

        String deviceId = request.getHeader("deviceId");
        UUID userId;
        try {
            userId = jwtProvider.extractUserId(refreshToken, true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_EXPIRED, null));
        }

        Optional<String> storedTokenOpt = tokenStoreService.getRefreshToken(userId, deviceId);
        if (storedTokenOpt.isEmpty() || !storedTokenOpt.get().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_INVALID_REFRESH_TOKEN, null));
        }

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        tokenStoreService.storeAccessToken(userId, deviceId, newAccessToken);

        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        tokenStoreService.storeRefreshToken(userId, deviceId, newRefreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_REFRESH_SUCCESS,
                Map.of("accessToken", newAccessToken)));
    }

    public ResponseEntity<ApiResponse<String>> logout(String accessToken, String deviceId) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        UUID userId;
        try {
            userId = jwtProvider.extractUserId(accessToken, false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_INVALID_ACCESS_TOKEN, null));
        }

        tokenStoreService.removeTokens(userId, deviceId);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        if (response != null) {
            response.addHeader("Set-Cookie", deleteCookie.toString());
        }

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGOUT_SUCCESS, null));
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> buildAuthResponse(
            User user, String deviceId, HttpServletResponse response) {

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        tokenStoreService.storeAccessToken(user.getId(), deviceId, accessToken);
        tokenStoreService.storeRefreshToken(user.getId(), deviceId, refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        UserDTO userDTO = userMapper.toUserDTO(user);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS,
                Map.of("accessToken", accessToken, "user", userDTO)));
    }
}
