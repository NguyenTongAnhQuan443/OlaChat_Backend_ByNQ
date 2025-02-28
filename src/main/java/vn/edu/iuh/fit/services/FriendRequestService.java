package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.constants.FriendRequestMessages;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.enums.FriendRequestStatus;
import vn.edu.iuh.fit.mappers.FriendRequestMapper;
import vn.edu.iuh.fit.models.FriendRequest;
import vn.edu.iuh.fit.models.Friendship;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.FriendRequestRepository;
import vn.edu.iuh.fit.repositories.FriendshipRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestMapper friendRequestMapper;

    //    Gửi lời mời kết bạn
    @Transactional
    public FriendRequestDTO sendFriendRequest(User sender, User receiver) {
        if (sender.equals(receiver)) {
            return null; // Không thể gửi lời mời cho chính mình
        }

        // Kiểm tra nếu đã có lời mời kết bạn giữa sender và receiver
        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);

        // Nếu đã có lời mời tồn tại và trạng thái là PENDING thì không gửi lại
        if (existingRequest.isPresent()) {
            if (existingRequest.get().getStatus() == FriendRequestStatus.PENDING) {
                return null; // Đã có lời mời kết bạn ở trạng thái chờ, không thể gửi lại
            } else if (existingRequest.get().getStatus() == FriendRequestStatus.ACCEPTED) {
                return null; // Đã là bạn bè, không thể gửi lại lời mời kết bạn
            }
        }

        // Kiểm tra trường hợp ngược lại: Nếu đã có lời mời kết bạn từ receiver tới sender và trạng thái là PENDING, không cho phép gửi lại
        Optional<FriendRequest> reverseRequest = friendRequestRepository.findBySenderAndReceiver(receiver, sender);

        // Nếu đã có lời mời từ phía người nhận cho người gửi và trạng thái là PENDING, không cho phép gửi lại
        if (reverseRequest.isPresent() && reverseRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            return null; // Đã có lời mời từ phía người nhận đang chờ, không thể gửi lại
        }


        // Kiểm tra trường hợp ngược lại: Nếu đã có lời mời kết bạn, nhưng đã được ACCEPTED hoặc DECLINED, cho phép gửi lại

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        friendRequestRepository.save(friendRequest);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    //    Hủy lời mời kết bạn
    @Transactional
    public FriendRequestDTO cancelFriendRequest(User sender, User receiver) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (friendRequest.isPresent() && friendRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            FriendRequestDTO friendRequestDTO = friendRequestMapper.toFriendRequestDTO(friendRequest.get());
            friendRequestRepository.delete(friendRequest.get());
            return friendRequestDTO;
        }
        return null;
    }

    //    Chấp nhận lời mời kết bạn
    @Transactional
    public FriendRequestDTO acceptFriendRequest(User receiver, User sender) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (friendRequest.isPresent() && friendRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            friendRequest.get().setStatus(FriendRequestStatus.ACCEPTED);
            friendRequestRepository.save(friendRequest.get());

            // Tạo quan hệ bạn bè
            Friendship friendship = Friendship.builder()
                    .user1(sender)
                    .user2(receiver)
                    .build();
            friendshipRepository.save(friendship);

            return friendRequestMapper.toFriendRequestDTO(friendRequest.get());
        }
        return null;
    }

    //    Từ chối lời mời kết bạn
    @Transactional
    public FriendRequestDTO declineFriendRequest(User receiver, User sender) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (friendRequest.isPresent() && friendRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            friendRequest.get().setStatus(FriendRequestStatus.DECLINED);
            friendRequestRepository.save(friendRequest.get());
            return friendRequestMapper.toFriendRequestDTO(friendRequest.get());
        }
        return null;
    }
}
