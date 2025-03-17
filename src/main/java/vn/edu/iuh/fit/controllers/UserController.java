package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.constants.UserConstants;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.utils.FormatPhoneNumber;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "User API", description = "Quản lý người dùng")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Gửi thông tin đăng ký và kiểm tra số điện thoại",
            description = "Đăng ký tài khoản bằng phoneNumber, displayName, password, email")
    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse<Object>> checkPhoneAndSendOtp(@Valid @RequestBody RegisterUserDTO registerUserDTO) {

        registerUserDTO.setPhoneNumber(FormatPhoneNumber.formatPhoneNumberTo84(registerUserDTO.getPhoneNumber()));

        userService.checkPhoneAndSendOtp(registerUserDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(200, UserConstants.MESSAGE_OTP_SENT, null)
        );
    }

    @Operation(summary = "Xác thực OTP và kích hoạt tài khoản",
            description = "Nhận OTP từ bước đăng ký TK SDT và xác thực OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<UserDTO>> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");

        User newUser = userService.verifyOtpAndRegisterUser(phoneNumber, otp);
        newUser.setPhoneNumber(FormatPhoneNumber.formatPhoneNumberTo84(phoneNumber));
        UserDTO userDTO = userMapper.toUserDTO(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(201, UserConstants.MESSAGE_OTP_VERIFIED, userDTO)
        );
    }

    @Operation(summary = "Lấy thông tin người dùng",
            description = "Trả về thông tin của người dùng dựa trên ID")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        UserDTO userDTO = userMapper.toUserDTO(user);
        return ResponseEntity.ok(new ApiResponse<>(200, UserConstants.MESSAGE_USER_INFO_RETRIEVED, userDTO));
    }

    @Operation(summary = "Lấy thông tin người dùng qua số điện thoại")
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        Optional<User> user = userService.getUserByPhonenumber(phoneNumber);
        UserDTO userDTO = userMapper.toUserDTO(user.orElse(null));
        return ResponseEntity.ok(new ApiResponse<>(200, UserConstants.MESSAGE_USER_INFO_RETRIEVED, userDTO));
    }

    @Operation(summary = "Yêu cầu OTP đặt lại mật khẩu qua email")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.sendPasswordResetOtp(email);
        return ResponseEntity.ok(UserConstants.MESSAGE_PASSWORD_RESET_OTP_SENT);
    }

    @Operation(summary = "Xác thực OTP và đặt lại mật khẩu")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        userService.resetPasswordWithOtp(email, otp, newPassword);
        return ResponseEntity.ok(UserConstants.MESSAGE_PASSWORD_UPDATED);
    }
}