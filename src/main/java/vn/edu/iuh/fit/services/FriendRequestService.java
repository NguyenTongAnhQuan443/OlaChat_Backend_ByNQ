package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.constants.CodeConstants;
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
import vn.edu.iuh.fit.services.interfaces.IFriendRequestService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendRequestService implements IFriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestMapper friendRequestMapper;

    @Transactional
    @Override
    public FriendRequestDTO sendFriendRequest(User sender, User receiver) {
        validateFriendRequest(sender, receiver);
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    @Transactional
    @Override
    public FriendRequestDTO cancelFriendRequest(User sender, User receiver) {
        FriendRequest friendRequest = findPendingRequest(sender, receiver);
        friendRequestRepository.delete(friendRequest);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    @Transactional
    @Override
    public FriendRequestDTO acceptFriendRequest(User sender, User receiver) {
        FriendRequest friendRequest = findPendingRequest(sender, receiver);
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        Friendship friendship = Friendship.builder()
                .user1(sender)
                .user2(receiver)
                .build();
        friendshipRepository.save(friendship);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    @Transactional
    @Override
    public FriendRequestDTO declineFriendRequest(User sender, User receiver) {
        FriendRequest friendRequest = findPendingRequest(sender, receiver);
        friendRequest.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(friendRequest);
        return friendRequestMapper.toFriendRequestDTO(friendRequest);
    }

    private void validateFriendRequest(User sender, User receiver) {
        if (sender.equals(receiver)) {
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_INVALID, null);
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findByUser1AndUser2(sender, receiver);
        if (existingFriendship.isPresent()) {
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_ALREADY_FRIENDS, null);
        }

        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest.isPresent() && existingRequest.get().getStatus() == FriendRequestStatus.PENDING) {
            throw new CustomException(CodeConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_PENDING, null);
        }
    }

    private FriendRequest findPendingRequest(User sender, User receiver) {
        return friendRequestRepository.findBySenderAndReceiver(sender, receiver)
                .filter(friendRequest -> friendRequest.getStatus() == FriendRequestStatus.PENDING)
                .orElseThrow(() -> new CustomException(CodeConstants.CODE_BAD_REQUEST, FriendRequestConstants.FRIEND_REQUEST_NOT_FOUND, null));
    }
}

