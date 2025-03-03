package vn.edu.iuh.fit.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.enums.FriendRequestStatus;
import vn.edu.iuh.fit.models.FriendRequest;
import vn.edu.iuh.fit.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Optional<FriendRequest> findBySenderAndReceiver(User userSender, User userReceiver);
}
