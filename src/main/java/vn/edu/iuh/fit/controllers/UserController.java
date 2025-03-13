package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.mappers.UserMapper;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.utils.ApiResponse;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "User API", description = "Quản lý người dùng")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản bằng số điện thoại")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        User newUser = userService.registerUser(registerUserDTO);
        UserDTO userDTO = userMapper.toUserDTO(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(201, "Đăng ký thành công!", userDTO)
        );
    }

    @Operation(summary = "Lấy thông tin người dùng", description = "Trả về thông tin của người dùng dựa trên ID")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        UserDTO userDTO = userMapper.toUserDTO(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành công!", userDTO));
    }

    @Operation(summary = "Lấy thông tin người dùng qua số điện thoại", description = "Trả về thông tin người dùng dựa trên số điện thoại")
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        Optional<User> user = userService.getUserByPhonenumber(phoneNumber);
        UserDTO userDTO = userMapper.toUserDTO(user.orElse(null));
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành công!", userDTO));
    }
}
