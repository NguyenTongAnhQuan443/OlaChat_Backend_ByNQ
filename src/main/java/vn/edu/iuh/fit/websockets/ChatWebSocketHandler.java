package vn.edu.iuh.fit.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import vn.edu.iuh.fit.services.ChatService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final String SENDER_ID = "senderId";
    private static final String RECEIVER_ID = "receiverId";
    private static final String MESSAGE = "message";
    private static final String USER_ID = "userId";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatService chatService;

    private UUID getUserIdFromSession(WebSocketSession session) {
        // Lấy userId từ headers
        String userIdStr = session.getHandshakeHeaders().getFirst(USER_ID);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    //  Lắng nghe connection
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        UUID userId = getUserIdFromSession(session);
        if (userId != null) {
            WebSocketSessionManager.addUserSession(userId, session);
            log.info("User Connected ID: " + userId);
        }
    }

    //  Nhận message
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        log.info("Server Received: " + message.getPayload());
        Map<String, String> msgData = objectMapper.readValue((String) message.getPayload(), Map.class);

        UUID senderId = UUID.fromString(msgData.get(SENDER_ID));
        UUID receiverId = UUID.fromString(msgData.get(RECEIVER_ID));
        String textMessage = msgData.get(MESSAGE);
        chatService.saveMessage(senderId, receiverId, textMessage);

        WebSocketSession receiverSession = WebSocketSessionManager.getUserSession(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msgData)));
        } else {
            log.warn("Receiver Is Not Online ID: ", receiverId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        UUID userId = getUserIdFromSession(session);
        if (userId != null) {
            WebSocketSessionManager.removeUserSession(userId);
            log.info("User Disconnected: " + userId);
        }
    }
}