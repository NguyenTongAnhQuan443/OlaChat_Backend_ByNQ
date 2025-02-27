package vn.edu.iuh.fit.models;

import lombok.Data;

@Data
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
}
