package vn.edu.iuh.fit.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.models.ChatMessage;
import vn.edu.iuh.fit.services.ChatService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{userId}")
    public List<ChatMessage> getChatHistory(@PathVariable UUID userId) {
        return chatService.getChatHistory(userId);
    }
}
