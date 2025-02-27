package vn.edu.iuh.fit.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.ChatMessage;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    public List<ChatMessage> getChatHistory(UUID userId) {
        return List.of(new ChatMessage());
    }
}
