package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.services.*;
import vn.edu.iuh.fit.utils.ApiResponse;

import java.util.Map;

@Tag(name = "Auth API", description = "Quản lý xác thực và phân quyền")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithGoogle(@RequestBody Map<String, String> request) {
        return authService.loginWithOAuth("GOOGLE", request.get("idToken"), request.get("deviceId"));

    }

    @PostMapping("/login-phone")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(@RequestBody Map<String, String> request) {
        return authService.loginWithPhoneNumber(request.get("phoneNumber"), request.get("password"), request.get("deviceId"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(@RequestBody Map<String, String> request) {
        return authService.refreshAccessToken(request.get("refreshToken"));
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody Map<String, String> request) {
        return authService.logout(request.get("accessToken"), request.get("deviceId"));
    }
}