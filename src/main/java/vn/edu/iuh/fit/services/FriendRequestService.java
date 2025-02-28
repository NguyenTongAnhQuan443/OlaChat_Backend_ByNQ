package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.constants.FriendRequestConstants;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.enums.FriendRequestStatus;
import vn.edu.iuh.fit.exceptions.CustomException;
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
//            Không thể gửi lời mời cho chính mình
            throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_INVALID, null);
        }

//        Kiểm tra nếu đã có quan hệ bạn bè giữa sender và receiver
        Optional<Friendship> existingFriendship1 = friendshipRepository.findByUser1AndUser2(sender, receiver);
        Optional<Friendship> existingFriendship2 = friendshipRepository.findByUser1AndUser2(receiver, sender);
        if (existingFriendship1.isPresent() || existingFriendship2.isPresent()) {
            throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_ALREADY_FRIENDS, null);
        }

//        Kiểm tra nếu đã có lời mời kết bạn giữa sender và receiver
        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest.isPresent()) {

            FriendRequest friendRequest = existingRequest.get();

            if (friendRequest.getStatus() == FriendRequestStatus.PENDING) {
//                Đã có lời mời kết bạn đang chờ, không thể gửi lại.
                throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_PENDING, null);
            }

            if (friendRequest.getStatus() == FriendRequestStatus.ACCEPTED) {
//                Bạn đã là bạn bè, không thể gửi lại lời mời.
                throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_ALREADY_FRIENDS, null);
            }
        }

//         Kiểm tra trường hợp ngược lại: nếu đã có lời mời kết bạn từ receiver tới sender
        Optional<FriendRequest> reverseRequest = friendRequestRepository.findBySenderAndReceiver(receiver, sender);
        if (reverseRequest.isPresent()) {
            FriendRequest reverseFriendRequest = reverseRequest.get();
            if (reverseFriendRequest.getStatus() == FriendRequestStatus.PENDING) {
//                Đã có lời mời kết bạn từ phía người nhận đang chờ.
                throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_REVERSED_PENDING, null);
            }
        }

        FriendRequest friendRequest = FriendRequest.builder().sender(sender).receiver(receiver).status(FriendRequestStatus.PENDING).build();

        friendRequestRepository.save(friendRequest);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    //    Hủy lời mời kết bạn
    @Transactional
    public FriendRequestDTO cancelFriendRequest(User sender, User receiver) {
        // Tìm lời mời kết bạn giữa sender và receiver
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);

        if (friendRequest.isPresent()) {
            FriendRequest friendRequestEntity = friendRequest.get();

            // Kiểm tra nếu trạng thái của lời mời là PENDING, có thể hủy
            if (friendRequestEntity.getStatus() == FriendRequestStatus.PENDING) {
                friendRequestRepository.delete(friendRequestEntity); // Xóa lời mời
                return friendRequestMapper.toFriendRequestDTO(friendRequestEntity);
            } else if (friendRequestEntity.getStatus() == FriendRequestStatus.ACCEPTED) {
                // Nếu lời mời đã được chấp nhận, không thể hủy
                throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST,
                        FriendRequestConstants.FRIEND_REQUEST_CANNOT_CANCEL, null);
            }
        }

        // Nếu không tìm thấy lời mời hoặc trạng thái không thể hủy, ném lỗi
        throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST,
                FriendRequestConstants.FRIEND_REQUEST_NOT_FOUND, null);
    }

    //    Chấp nhận lời mời kết bạn
    @Transactional
    public FriendRequestDTO acceptFriendRequest(User receiver, User sender) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (friendRequest.isPresent() && friendRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            friendRequest.get().setStatus(FriendRequestStatus.ACCEPTED);
            friendRequestRepository.save(friendRequest.get());

            // Tạo quan hệ bạn bè
            Friendship friendship = Friendship.builder().user1(sender).user2(receiver).build();
            friendshipRepository.save(friendship);

            return friendRequestMapper.toFriendRequestDTO(friendRequest.get());
        }
        throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST,
                FriendRequestConstants.FRIEND_REQUEST_NOT_FOUND, null);
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
        throw new CustomException(FriendRequestConstants.CODE_BAD_REQUEST,
                FriendRequestConstants.FRIEND_REQUEST_CANNOT_DECLINE, null);
    }
}
