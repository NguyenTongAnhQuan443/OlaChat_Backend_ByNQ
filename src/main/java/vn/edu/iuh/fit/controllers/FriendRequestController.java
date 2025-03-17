package vn.edu.iuh.fit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dtos.FriendRequestActionDTO;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.services.interfaces.IFriendRequestService;
import vn.edu.iuh.fit.utils.ApiResponse;

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
    public ResponseEntity<ApiResponse<FriendRequestDTO>> sendFriendRequest(
            @Valid @RequestBody FriendRequestActionDTO request) {
        return handleFriendRequest(request, friendRequestService::sendFriendRequest, "Gửi lời mời kết bạn");
    }

    @Operation(summary = "Hủy yêu cầu kết bạn")
    @DeleteMapping("/cancel/{senderId}/{receiverId}")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> cancelFriendRequest(
            @PathVariable UUID senderId, @PathVariable UUID receiverId) {
        return handleFriendRequest(new FriendRequestActionDTO(senderId, receiverId), friendRequestService::cancelFriendRequest, "Hủy lời mời kết bạn");
    }

    @Operation(summary = "Chấp nhận lời mời kết bạn")
    @PutMapping("/accept")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> acceptFriendRequest(
            @Valid @RequestBody FriendRequestActionDTO request) {
        return handleFriendRequest(request, friendRequestService::acceptFriendRequest, "Chấp nhận lời mời kết bạn");
    }

    @Operation(summary = "Từ chối lời mời kết bạn")
    @PutMapping("/decline")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> declineFriendRequest(
            @Valid @RequestBody FriendRequestActionDTO request) {
        return handleFriendRequest(request, friendRequestService::declineFriendRequest, "Từ chối lời mời kết bạn");
    }

    private ResponseEntity<ApiResponse<FriendRequestDTO>> handleFriendRequest(
            FriendRequestActionDTO request, BiFunction<User, User, FriendRequestDTO> action, String actionName) {
        User sender = userService.getUserById(request.getSender()).get();
        User receiver = userService.getUserById(request.getReceiver()).get();
        return ResponseEntity.ok(new ApiResponse<>(200, actionName + " thành công", action.apply(sender, receiver)));
    }
}
