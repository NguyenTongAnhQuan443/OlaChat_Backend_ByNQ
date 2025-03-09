package vn.edu.iuh.fit.services.interfaces;

import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.models.User;

public interface IFriendRequestService {
    FriendRequestDTO sendFriendRequest(User sender, User receiver);

    FriendRequestDTO cancelFriendRequest(User sender, User receiver);

    FriendRequestDTO acceptFriendRequest(User sender, User receiver);

    FriendRequestDTO declineFriendRequest(User sender, User receiver);
}