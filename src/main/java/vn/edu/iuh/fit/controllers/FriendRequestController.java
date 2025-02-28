package vn.edu.iuh.fit.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.FriendRequestService;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.utils.ApiResponse;
import vn.edu.iuh.fit.constants.FriendRequestMessages;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;
    private final UserService userService;

    //    Gửi lời mời kết bạn
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> sendFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get("senderId");
        UUID receiverId = requestBody.get("receiverId");

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        FriendRequestDTO result = friendRequestService.sendFriendRequest(sender, receiver);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestMessages.FRIEND_REQUEST_SENT, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Gửi lời mời kết bạn thất bại.", null));
        }
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> cancelFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get("senderId");
        UUID receiverId = requestBody.get("receiverId");

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        FriendRequestDTO result = friendRequestService.cancelFriendRequest(sender, receiver);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestMessages.FRIEND_REQUEST_CANCELLED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestMessages.FRIEND_REQUEST_CANNOT_CANCEL, null));
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> acceptFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get("senderId");
        UUID receiverId = requestBody.get("receiverId");

        User receiver = userService.getUserById(receiverId);
        User sender = userService.getUserById(senderId);
        FriendRequestDTO result = friendRequestService.acceptFriendRequest(receiver, sender);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestMessages.FRIEND_REQUEST_ACCEPTED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestMessages.FRIEND_REQUEST_CANNOT_ACCEPT, null));
        }

    }

    @PostMapping("/decline")
    public ResponseEntity<ApiResponse<FriendRequestDTO>> declineFriendRequest(@RequestBody Map<String, UUID> requestBody) {
        UUID senderId = requestBody.get("senderId");
        UUID receiverId = requestBody.get("receiverId");

        User receiver = userService.getUserById(receiverId);
        User sender = userService.getUserById(senderId);
        FriendRequestDTO result = friendRequestService.declineFriendRequest(receiver, sender);

        if (result != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), FriendRequestMessages.FRIEND_REQUEST_DECLINED, result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), FriendRequestMessages.FRIEND_REQUEST_CANNOT_DECLINE, null));
        }

    }
}
