package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dtos.LoginRequestDTO;
import vn.edu.iuh.fit.dtos.LogoutRequestDTO;
import vn.edu.iuh.fit.dtos.PhoneLoginRequestDTO;
import vn.edu.iuh.fit.dtos.RefreshTokenRequestDTO;
import vn.edu.iuh.fit.services.Authentication.AuthenticationService;
import vn.edu.iuh.fit.utils.ApiResponse;

import java.util.Map;

@Tag(name = "Authentication API", description = "Quản lý xác thực người dùng")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @Operation(summary = "Đăng nhập bằng GOOGLE", description = "Frontend gửi idToken và deviceId lên để thực hiện đăng nhập")
    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithGoogle(
            @Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        return authService.loginWithOAuth("GOOGLE", request.getIdToken(), request.getDeviceId(), response);
    }

    @Operation(summary = "Đăng nhập bằng SDT", description = "Dùng SDT để đăng nhập")
    @PostMapping("/login-phone")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithPhoneNumber(
            @Valid @RequestBody PhoneLoginRequestDTO request, HttpServletResponse response) {
        return authService.loginWithPhoneNumber(request.getPhoneNumber(), request.getPassword(), request.getDeviceId(), response);
    }

    @Operation(summary = "Làm mới Access Token", description = "Dùng refreshToken từ HTTP-only Cookie để lấy accessToken mới.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse response) {
        return authService.refreshAccessToken(httpRequest, response);
    }

    @Operation(summary = "Đăng xuất", description = "Dùng accessToken để đăng xuất")
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutRequestDTO request) {
        return authService.logout(request.getAccessToken(), request.getDeviceId());
    }
}
