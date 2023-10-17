package com.youlai.system.config;

import com.youlai.system.interceptor.WebsocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置
 *
 * @author haoxr
 * @since 2.4.0
 */
@Configuration
@ConditionalOnProperty(name = "system.config.websocket-enabled")// system.config.websocket-enabled = true 才会自动装配
@EnableWebSocketMessageBroker // 启用WebSocket消息代理功能和配置STOMP协议，实现实时双向通信和消息传递
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebsocketChannelInterceptor websocketChannelInterceptor;

    /**
     * 注册一个端点，客户端通过这个端点进行连接
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")   // 注册了一个 /ws 的端点
                .setAllowedOriginPatterns("*") // 允许跨域的 WebSocket 连接
                .withSockJS();  // 启用 SockJS (浏览器不支持WebSocket，SockJS 将会提供兼容性支持)
        registry.addEndpoint("/ws-app").setAllowedOriginPatterns("*");  // 注册了一个 /ws-app 的端点，支持 uni-app 的 ws 连接协议
    }


    /**
     * 配置消息代理
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


    /**
     * 配置客户端入站通道拦截器
     *
     * @param registration 通道注册器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(websocketChannelInterceptor);
    }
}
