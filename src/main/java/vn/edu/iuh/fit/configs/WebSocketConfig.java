package vn.edu.iuh.fit.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import vn.edu.iuh.fit.websockets.MessageHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    /**
     * http://localhost:8080/ws/chat
     * Register WebSocketHandlers
     *
     * @param registry
     */

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getMessageHandler(), "/ws/chat").setAllowedOrigins("*");
    }

    @Bean
    public MessageHandler getMessageHandler() {
        return new MessageHandler();
    }
}
