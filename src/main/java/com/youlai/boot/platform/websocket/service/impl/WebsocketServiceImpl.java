package com.youlai.boot.platform.websocket.service.impl;

import com.youlai.system.event.UserConnectionEvent;
import com.youlai.system.model.dto.ChatMessage;
import com.youlai.system.service.WebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebsocketServiceImpl implements WebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // 在线用户
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    // 离线消息
    private final Set<ChatMessage> offlineMessages = ConcurrentHashMap.newKeySet();

    private final Map<ChatMessage,Set<String>> messageReceiptStatus = new ConcurrentHashMap<>();

    @Override
    public void addUser(String username) {
        onlineUsers.add(username);
    }

    @Override
    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    @Override
    public Set<String> getUsers() {
        return onlineUsers;
    }

    @EventListener
    public void handleUserConnectionEvent(UserConnectionEvent event) {
        String username = event.getUsername();
        if (event.isConnected()) {
            onlineUsers.add(username);
            log.info("User connected: {}", username);
            // 发送离线消息
            offlineMessages.forEach(message -> {
                messagingTemplate.convertAndSendToUser(username, "/topic/chat", message);
                messageReceiptStatus.computeIfAbsent(message, k -> ConcurrentHashMap.newKeySet()).add(username);
            });
        } else {
            onlineUsers.remove(username);
            log.info("User disconnected: {}", username);
        }
        // 推送在线用户人数
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUsers.size());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        onlineUsers.remove(username);
    }

    @Scheduled(fixedRate = 5000)
    public void sendOnlineUserCount() {
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUsers.size());
    }

    @Override
    public void sendStringToFrontend(String sender, String message) {
        ChatMessage chatMessage = new ChatMessage(sender, message);
        offlineMessages.add(chatMessage);
        messageReceiptStatus.putIfAbsent(chatMessage, ConcurrentHashMap.newKeySet());
        onlineUsers.forEach(receiver -> {
            messagingTemplate.convertAndSendToUser(receiver, "/topic/chat", chatMessage);
            messageReceiptStatus.get(chatMessage).add(receiver);
        });
        if(messageReceiptStatus.get(chatMessage).size() == onlineUsers.size()) {
            //记录完成状态
            offlineMessages.remove(chatMessage);//从离线消息中移除已发送的消息
            messageReceiptStatus.remove(chatMessage);//从消息接收状态集合总移除已发送的消息

        }
    }
}
