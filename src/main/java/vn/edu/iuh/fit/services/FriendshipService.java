package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.Friendship;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.FriendshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public List<Friendship> getFriendList(User user) {
        return friendshipRepository.findByUser1OrUser2(user, user);
    }

    public boolean areFriends(User user1, User user2) {
        return friendshipRepository.findByUser1OrUser2(user1, user2)
                .stream()
                .anyMatch(friendship ->
                        (friendship.getUser1().equals(user1) && friendship.getUser2().equals(user2)) ||
                                (friendship.getUser1().equals(user2) && friendship.getUser2().equals(user1))
                );
    }
}
