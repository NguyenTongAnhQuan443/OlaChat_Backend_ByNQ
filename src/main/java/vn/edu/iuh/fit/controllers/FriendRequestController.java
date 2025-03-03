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
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.constants.FriendRequestConstants;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Friend Request API",
        description = "Quản lý chức năng gửi, hủy, chấp nhận, từ chối lời mời kết bạn")
@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private static final String SENDER_ID = "senderId";
    private static final String RECEIVER_ID = "receiverId";

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    @Operation(summary = "Gửi lời mời kết bạn")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> sendFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get(SENDER_ID);
        UUID receiverId = requestBody.get(RECEIVER_ID);

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        FriendRequestDTO result = friendRequestService.sendFriendRequest(sender, receiver);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestConstants.FRIEND_REQUEST_SENT, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestConstants.FRIEND_REQUEST_FAILED, null));
        }
    }

    @Operation(summary = "Hủy yêu cầu kết bạn")
    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> cancelFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get(SENDER_ID);
        UUID receiverId = requestBody.get(RECEIVER_ID);

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        FriendRequestDTO result = friendRequestService.cancelFriendRequest(sender, receiver);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestConstants.FRIEND_REQUEST_CANCELLED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestConstants.FRIEND_REQUEST_CANNOT_CANCEL, null));
        }
    }

    @Operation(summary = "Chấp nhận lời mời kết bạn")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> acceptFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get(SENDER_ID);
        UUID receiverId = requestBody.get(RECEIVER_ID);

        User receiver = userService.getUserById(receiverId);
        User sender = userService.getUserById(senderId);
        FriendRequestDTO result = friendRequestService.acceptFriendRequest(receiver, sender);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestConstants.FRIEND_REQUEST_ACCEPTED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestConstants.FRIEND_REQUEST_CANNOT_ACCEPT, null));
        }

    }

    @Operation(summary = "Từ chối lời mời kết bạn")
    @PostMapping("/decline")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> declineFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get(SENDER_ID);
        UUID receiverId = requestBody.get(RECEIVER_ID);

        User receiver = userService.getUserById(receiverId);
        User sender = userService.getUserById(senderId);
        FriendRequestDTO result = friendRequestService.declineFriendRequest(receiver, sender);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestConstants.FRIEND_REQUEST_DECLINED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestConstants.FRIEND_REQUEST_CANNOT_DECLINE, null));
        }

    }
}
