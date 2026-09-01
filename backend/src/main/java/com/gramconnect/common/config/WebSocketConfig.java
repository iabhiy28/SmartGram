package com.gramconnect.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket & STOMP Configuration for real-time village notifications & emergency broadcasts.
 *
 * Endpoints:
 *   - Handshake: /ws
 *   - User Queue: /user/queue/notifications
 *   - Village Topic: /topic/village/{villageId}/announcements
 *   - App Inbound Prefix: /app
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // In-memory message broker for subscription destinations
        config.enableSimpleBroker("/topic", "/queue");
        // Destination prefix for messages routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific queues (e.g. /user/{userId}/queue/notifications)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register /ws handshake endpoint with SockJS fallback and CORS support
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
