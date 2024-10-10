package com.youlai.boot.shared.websocket.listener;

import com.youlai.boot.shared.websocket.service.OnlineUserService;
import com.youlai.boot.system.event.UserConnectionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 在线用户监听器
 *
 * @author haoxr
 * @since 2024/9/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OnlineUserListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserService onlineUserService;

    /**
     * 用户连接事件处理
     *
     * @param event 用户连接事件
     */
    @EventListener
    public void handleUserConnectionEvent(UserConnectionEvent event) {
        String username = event.getUsername();
        if (event.isConnected()) {
            onlineUserService.addOnlineUser(username);
            log.info("User connected: {}", username);
        } else {
            onlineUserService.removeOnlineUser(username);
            log.info("User disconnected: {}", username);
        }
        // 推送在线用户人数
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUserService.getOnlineUserCount());
    }

}
