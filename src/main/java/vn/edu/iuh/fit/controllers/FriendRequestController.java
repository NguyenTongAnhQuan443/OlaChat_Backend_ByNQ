package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.FriendRequestService;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.services.interfaces.IFriendRequestService;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.constants.FriendRequestConstants;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Tag(name = "Friend Request API", description = "Quản lý chức năng gửi, hủy, chấp nhận, từ chối lời mời kết bạn")
@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final IFriendRequestService friendRequestService;
    private final UserService userService;

    @Operation(summary = "Gửi lời mời kết bạn")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> sendFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        return handleFriendRequest(requestBody, friendRequestService::sendFriendRequest, "Gửi lời mời kết bạn");
    }

    @Operation(summary = "Hủy yêu cầu kết bạn")
    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> cancelFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        return handleFriendRequest(requestBody, friendRequestService::cancelFriendRequest, "Hủy lời mời kết bạn");
    }

    @Operation(summary = "Chấp nhận lời mời kết bạn")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> acceptFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        return handleFriendRequest(requestBody, friendRequestService::acceptFriendRequest, "Chấp nhận lời mời kết bạn");
    }

    @Operation(summary = "Từ chối lời mời kết bạn")
    @PostMapping("/decline")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> declineFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        return handleFriendRequest(requestBody, friendRequestService::declineFriendRequest, "Từ chối lời mời kết bạn");
    }

    private ResponseEntity<ApiResponse<FriendRequestDTO>> handleFriendRequest(Map<String, UUID> requestBody, BiFunction<User, User, FriendRequestDTO> action, String actionName) {
        User sender = userService.getUserById(requestBody.get("sender"));
        User receiver = userService.getUserById(requestBody.get("receiver"));
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), actionName + "Thành công", action.apply(sender, receiver)));
    }
}
