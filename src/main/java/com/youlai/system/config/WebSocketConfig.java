package com.youlai.system.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * <p>
 * STOMP 不是一个传输协议，而是一个简单的文本消息协议，定义消息格式和交换规则
 *
 * @author haoxr
 * @since 2.3.0
 */
@Configuration
@ConditionalOnProperty(name = "system.config.websocket-enabled")// system.config.websocket-enabled = true 才会自动装配
@EnableWebSocketMessageBroker // 启用WebSocket消息代理功能和配置STOMP协议，实现实时双向通信和消息传递
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * 配置和注册WebSocket端点(endpoints)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")   // 注册了一个 /ws 的端点
                .setAllowedOriginPatterns("*") // 允许跨域的 WebSocket 连接
                .withSockJS()  // 启用 SockJS (浏览器不支持WebSocket，SockJS 将会提供兼容性支持)
        ;
    }


    /**
     * 配置消息代理(Message Broker)
     * <p>
     * 设置消息传输的规则和前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端发送消息的请求前缀
        registry.setApplicationDestinationPrefixes("/app");

        // 客户端订阅消息的请求前缀，topic一般用于广播推送，queue用于点对点推送
        registry.enableSimpleBroker("/topic", "/queue");

        // 服务端通知客户端的前缀，可以不设置，默认为user
        registry.setUserDestinationPrefix("/user");
    }

}
