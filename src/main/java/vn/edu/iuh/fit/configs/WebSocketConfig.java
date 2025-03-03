package vn.edu.iuh.fit.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import vn.edu.iuh.fit.services.ChatService;
import vn.edu.iuh.fit.websockets.ChatWebSocketHandler;
import vn.edu.iuh.fit.websockets.NotificationWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatService chatService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getChatHandler(), "/ws/chat").setAllowedOrigins("*");
        registry.addHandler(getNotificationHandler(), "/ws/notification").setAllowedOrigins("*");
    }

    @Bean
    public ChatWebSocketHandler getChatHandler() {
        return new ChatWebSocketHandler(chatService);
    }

    @Bean
    public NotificationWebSocketHandler getNotificationHandler() {
        return new NotificationWebSocketHandler();
    }
}
