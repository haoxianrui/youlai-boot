package com.youlai.boot.system.controller;

import com.youlai.boot.core.security.model.SysUserDetails;
import com.youlai.boot.system.service.WebSocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket消息控制器
 * 用于处理WebSocket客户端发送的消息
 *
 * @author You Lai
 * @since 3.0.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {

    private final WebSocketMessageService webSocketMessageService;

    /**
     * 处理发送到指定用户的消息
     * 客户端发送消息到 /app/sendToUser/{username}
     *
     * @param message 消息内容
     * @param headerAccessor 消息头访问器
     * @param username 接收消息的用户名
     */
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(@Payload String message, SimpMessageHeaderAccessor headerAccessor, String username) {
        Authentication authentication = (Authentication) headerAccessor.getUser();
        if (authentication != null) {
            SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
            String sender = userDetails.getUsername();
            
            // 构建消息
            Map<String, Object> messageData = Map.of(
                "sender", sender,
                "content", message,
                "timestamp", System.currentTimeMillis()
            );
            
            // 发送点对点消息
            webSocketMessageService.sendPrivateMessage(username, messageData);
            log.info("用户[{}]向用户[{}]发送消息: {}", sender, username, message);
        }
    }

    /**
     * 处理广播消息
     * 客户端发送消息到 /app/broadcast
     *
     * @param message 消息内容
     * @param headerAccessor 消息头访问器
     */
    @MessageMapping("/broadcast")
    public void broadcast(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = (Authentication) headerAccessor.getUser();
        if (authentication != null) {
            SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
            String sender = userDetails.getUsername();
            
            // 构建消息
            Map<String, Object> messageData = Map.of(
                "sender", sender,
                "content", message,
                "timestamp", System.currentTimeMillis()
            );
            
            // 发送广播消息
            webSocketMessageService.broadcastMessage(messageData);
            log.info("用户[{}]发送广播消息: {}", sender, message);
        }
    }
} 