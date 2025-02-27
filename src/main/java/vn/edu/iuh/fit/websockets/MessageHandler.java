package vn.edu.iuh.fit.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import vn.edu.iuh.fit.enums.NQLog;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class MessageHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    //  Lắng nghe connection
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        UUID userId = getUserIdFromSession(session);
        if (userId != null) {
            UserSessionManager.addUserSession(userId, session);
            log.info("User Connected ID: " + userId);
        }

    }

    //  Nhận message
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        log.info("SERVER Received: " + message.getPayload());

        Map<String, String> msgData = objectMapper.readValue((String) message.getPayload(), Map.class);
        UUID senderId = UUID.fromString(msgData.get("senderId"));
        UUID receiverId = UUID.fromString(msgData.get("receiverId"));
        String text = msgData.get("message");

        //  Tìm kiếm WebSocketSession của người nhận
        WebSocketSession receiverSession = UserSessionManager.getUserSession(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msgData)));
        } else {
            log.warn("Receiver {} Is Not Online.", receiverId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        UUID userId = getUserIdFromSession(session);
        if (userId != null) {
            UserSessionManager.removeUserSession(userId);
            log.info("User disconnected: " + userId);
        }
    }

    //
    private UUID getUserIdFromSession(WebSocketSession session) {
        // Lấy userId từ headers
        String userIdStr = session.getHandshakeHeaders().getFirst("userId");
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }
}
