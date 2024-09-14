package com.youlai.boot.module.websocket.service.impl;

import com.youlai.boot.common.enums.NoticeWayEnum;
import com.youlai.boot.common.enums.NoticeTypeEnum;
import com.youlai.boot.platform.websocket.service.MessageService;
import com.youlai.boot.system.event.UserConnectionEvent;
import com.youlai.boot.system.model.dto.ChatMessage;
import com.youlai.boot.system.model.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket消息服务实现类
 *
 * @author ray
 * @since 2024-9-2 14:32:58
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebsocketServiceImpl implements MessageService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    /**
     * 用户连接事件处理
     *
     * @param event 用户连接事件
     */
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

    /**
     * 定时推送在线用户人数
     */
    @Scheduled(fixedRate = 5000)
    public void sendOnlineUserCount() {
        log.info("Send online user count: {}", onlineUsers.size());
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUsers.size());
    }


    /**
     * 策略模式检查
     *
     * @param noticeWayEnum 通知方式
     * @return boolean 是否支持
     */
    @Override
    public boolean check(NoticeWayEnum noticeWayEnum) {
        return noticeWayEnum.equals(NoticeWayEnum.WEBSOCKET);
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */
    @Override
    public void sendMessage(MessageDTO message) {
        List<String> users = null;
        if(message.getReceiver() == null || message.getReceiver().isEmpty()){
            // 发送给所有在线用户 离线用户不发送，因为离线用户下次登录会直接查询未读消息
            users = new ArrayList<>(onlineUsers);
        }else{
            users = message.getReceiver().stream().filter(onlineUsers::contains).collect(Collectors.toList());
        }
        //获取当前用户
        ChatMessage chatMessage = new ChatMessage(message.getSender(), message.getContent(), NoticeTypeEnum.SYSTEM_MESSAGE);
        users.forEach(receiver -> {
            messagingTemplate.convertAndSendToUser(receiver, "/queue/message", chatMessage);
        });
    }
}
