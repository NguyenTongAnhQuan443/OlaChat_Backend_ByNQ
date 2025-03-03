package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.enums.MessageStatus;
import vn.edu.iuh.fit.models.ChatConversation;
import vn.edu.iuh.fit.models.ChatMessage;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.ChatConversationRepository;
import vn.edu.iuh.fit.repositories.ChatMessageRepository;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatConversationRepository chatConversationRepository;
    private final UserRepository userRepository;

    public void saveMessage(UUID senderId, UUID receiverId, String message) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(chatMessage);

        // Cập nhật lịch sử cuộc trò chuyện
        updateConversation(sender, receiver);
    }

    private void updateConversation(User sender, User receiver) {
        Optional<ChatConversation> conversation = chatConversationRepository
                .findByUser1OrUser2OrderByLastMessageTimeDesc(sender, receiver)
                .stream().findFirst();

        ChatConversation chatConversation;
        if (conversation.isPresent()) {
            chatConversation = conversation.get();
        } else {
            chatConversation = ChatConversation.builder()
                    .user1(sender)
                    .user2(receiver)
                    .build();
        }
        chatConversation.setLastMessageTime(LocalDateTime.now());
        chatConversationRepository.save(chatConversation);
    }
}
