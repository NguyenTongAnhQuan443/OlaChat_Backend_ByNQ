package vn.edu.iuh.fit.websockets;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {

    private static final ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public static void addUserSession(UUID userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public static WebSocketSession getUserSession(UUID userId) {
        return sessions.get(userId);
    }

    public static void removeUserSession(UUID userId) {
        sessions.remove(userId);
    }
}
