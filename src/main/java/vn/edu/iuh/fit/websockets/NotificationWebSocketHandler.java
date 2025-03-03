package vn.edu.iuh.fit.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final String USER_ID = "userId";
    private static final Map<UUID, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userIdStr = session.getHandshakeHeaders().getFirst(USER_ID);
        if (userIdStr != null) {
            UUID userId = UUID.fromString(userIdStr);
            activeSessions.put(userId, session);
            log.info("Notifications Connected ID: ", userId);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received Notification Message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        activeSessions.values().remove(session);
        log.info("User Disconnected ID: ");
    }

    public static void sendNotification(UUID receiverId, String message) throws Exception {
        WebSocketSession session = activeSessions.get(receiverId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            log.warn("Receiver Is Not Online ID: ", receiverId);
        }
    }
}
