package com.youlai.system.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.youlai.system.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
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
@EnableWebSocketMessageBroker // 启用WebSocket消息代理功能和配置STOMP协议，实现实时双向通信和消息传递
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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
     * <p>
     * 添加 ChannelInterceptor 拦截器，用于在消息发送前，从请求头中获取 token 并解析出用户信息(username)，用于点对点发送消息给指定用户
     *
     * @param registration 通道注册器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                // 如果是连接请求（CONNECT 命令），从请求头中取出 token 并设置到认证信息中
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 从连接头中提取授权令牌
                    String bearerToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

                    // 验证令牌格式并提取用户信息
                    if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
                        try {
                            // 移除 "Bearer " 前缀，从令牌中提取用户信息(username), 并设置到认证信息中
                            bearerToken = bearerToken.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
                            String username = JWTUtil.parseToken(bearerToken).getPayloads().getStr(JWTPayload.SUBJECT);
                            if (StrUtil.isNotBlank(username)) {
                                accessor.setUser(() -> username);
                                return message;
                            }
                        } catch (Exception e) {
                            log.error("Failed to process authentication token.", e);
                        }
                    }
                }
                // 不是连接请求，直接放行
                return ChannelInterceptor.super.preSend(message, channel);
            }
        });
    }

}
