package com.youlai.system.plugin.websocket;

import cn.hutool.core.util.StrUtil;
import com.youlai.system.base.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Websocket 连接认证拦截器
 *
 * @author haoxr
 * @since 2.4.0
 */
@Component
@RequiredArgsConstructor
public class WebsocketChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 连接前监听
     *
     * @param message 消息
     * @param channel 通道
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (StrUtil.isNotBlank(bearerToken)) {
                bearerToken = bearerToken.substring(7); // remove "Bearer "
                String username = jwtTokenProvider.getUsername(bearerToken);
                if (StrUtil.isNotBlank(username)) {
                    Principal principal = () -> username;
                    accessor.setUser(principal);
                    return message;
                }
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

}
