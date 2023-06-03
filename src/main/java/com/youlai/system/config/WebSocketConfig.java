package com.youlai.system.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 *
 * @author haoxr
 * @since 2.3.0
 */
@Configuration
@ConditionalOnProperty(name = "system.config.websocket-enabled")// system.config.websocket-enabled = true 才会自动装配
@EnableWebSocketMessageBroker // 注解告诉Spring框架要开启WebSocket消息代理的功能，并配置相关的端点和消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册了一个 /ws 的端点，用于建立WebSocket连接。
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }
}
