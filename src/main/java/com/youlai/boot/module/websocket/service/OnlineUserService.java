package com.youlai.boot.module.websocket.service;

import com.youlai.boot.system.event.UserConnectionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户服务
 *
 * @author Ray
 * @since 2.3.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineUserService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleUserConnectionEvent(UserConnectionEvent event) {
        String username = event.getUsername();
        if (event.isConnected()) {
            onlineUsers.add(username);
            log.info("User connected: {}", username);
        } else {
            onlineUsers.remove(username);
            log.info("User disconnected: {}", username);
        }
        // 推送在线用户人数
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUsers.size());
    }

    @Scheduled(fixedRate = 5000)
    public void sendOnlineUserCount() {
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUsers.size());
    }
}
