package vn.edu.iuh.fit.services.OAuthServices;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.constants.CodeConstants;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.services.RefreshTokenService;
import vn.edu.iuh.fit.services.TokenBlacklistService;
import vn.edu.iuh.fit.services.interfaces.IOAuth.IOAuthVerifier;
import vn.edu.iuh.fit.services.interfaces.IOAuth.IUserProvider;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.utils.JwtUtil;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final OAuthServiceFactory oAuthServiceFactory;

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithOAuth(String provider, String idToken, String deviceId) {
        try {
            IOAuthVerifier verifier = oAuthServiceFactory.getOAuthVerifier(provider);
            IUserProvider userProvider = oAuthServiceFactory.getUserProvider(provider);

            String accessToken = verifier.verifyToken(idToken);
            User user = userProvider.getUserFromToken(idToken);

            // Kiểm tra User đã có refreshToken hay chưa
            Optional<String> existingRefreshToken = refreshTokenService.findByUserAndDevice(user.getId(), deviceId);
            String refreshToken = existingRefreshToken.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId(), deviceId));

            return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of("accessToken", accessToken)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, "OAuth token không hợp lệ !", null));
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(String phoneNumber, String password, String deviceId) {
        Optional<User> userOpt = userRepository.findUserByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_LOGIN_FAILED, null));
        }

        User user = userOpt.get();
        String accessToken = jwtUtil.generateToken(user.getId());
        UserDTO userDTO = userMapper.toUserDTO(user);

        Optional<String> existingRefreshToken = refreshTokenService.findByUserAndDevice(user.getId(), deviceId);
        String refreshToken = existingRefreshToken.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId(), deviceId));

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGIN_SUCCESS, Map.of("accessToken", accessToken)));
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(String refreshToken, String deviceId) {
        if (refreshToken == null || refreshToken.isEmpty() || deviceId == null || deviceId.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(CodeConstants.CODE_BAD_REQUEST, AuthConstants.MESSAGE_REFRESH_TOKEN_REQUIRED, null));
        }

        Optional<String> tokenOptional = refreshTokenService.findByTokenAndDevice(refreshToken, deviceId);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_INVALID, null));
        }

        Optional<String> userIdOptional = refreshTokenService.findUserIdByDeviceAndToken(deviceId, refreshToken);
        if (userIdOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(CodeConstants.CODE_FORBIDDEN, AuthConstants.MESSAGE_REFRESH_TOKEN_INVALID, null));
        }

        String userId = userIdOptional.get();
        String newAccessToken = jwtUtil.generateToken(UUID.fromString(userId));
        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_REFRESH_TOKEN_SUCCESS, Map.of("accessToken", newAccessToken)));
    }


    public ResponseEntity<ApiResponse<String>> logout(String accessToken, String deviceId) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        UUID userId;
        try {
            userId = jwtUtil.extractUserId(accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(CodeConstants.CODE_UNAUTHORIZED, AuthConstants.MESSAGE_INVALID_ACCESS_TOKEN, null));
        }

        // 🔹 Kiểm tra xem user có refreshToken trên deviceId này không
        Optional<String> tokenOptional = refreshTokenService.findByUserAndDevice(userId, deviceId);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(CodeConstants.CODE_NOT_FOUND, AuthConstants.MESSAGE_DEVICE_LOGOUT_NOT_FOUND, null));
        }

        // 🔹 Xóa refresh token khỏi Redis
        refreshTokenService.deleteByUserAndDevice(userId.toString(), deviceId);

        // 🔹 Thêm accessToken vào blacklist để chặn sử dụng lại
        tokenBlacklistService.addToBlacklist(accessToken);

        return ResponseEntity.ok(new ApiResponse<>(CodeConstants.CODE_SUCCESS, AuthConstants.MESSAGE_LOGOUT_SUCCESS, null));
    }

}
