package com.youlai.boot.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.event.DictEvent;
import com.youlai.boot.system.service.WebSocketService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket服务实现类
 * 统一处理WebSocket消息发送和用户在线状态管理
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    // 在线用户映射表，key为用户名，value为用户在线信息
    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();
    
    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("WebSocket消息模板已初始化");
    }

    //==================================
    //      用户在线状态管理功能
    //==================================

    /**
     * 用户上线
     *
     * @param username  用户名
     * @param sessionId WebSocket会话ID（可选）
     */
    @Override
    public void userConnected(String username, String sessionId) {
        // 生成会话ID（如果未提供）
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("用户[{}]上线，当前在线用户数：{}", username, onlineUsers.size());
        
        // 通知在线用户状态变更
        notifyOnlineUsersChangeInternal();
    }

    /**
     * 用户下线
     *
     * @param username 用户名
     */
    @Override
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        log.info("用户[{}]下线，当前在线用户数：{}", username, onlineUsers.size());
        
        // 通知在线用户状态变更
        notifyOnlineUsersChangeInternal();
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户名列表
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return onlineUsers.values().stream()
                .map(info -> new UserOnlineDTO(info.getUsername(), info.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return 是否在线
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }
    
    /**
     * 手动触发在线用户变更通知
     * 供外部手动触发通知使用
     */
    public void notifyOnlineUsersChange() {
        log.info("手动触发在线用户数量通知，当前在线用户数：{}", onlineUsers.size());
        sendOnlineUserCount();
    }
    
    /**
     * 发送在线用户数量（简化版，不包含用户详情）
     */
    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送在线用户数量");
            return;
        }
        
        try {
            // 直接发送数量，更轻量
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("已发送在线用户数量: {}", count);
        } catch (Exception e) {
            log.error("发送在线用户数量失败", e);
        }
    }
    
    /**
     * 内部通用通知方法
     * 通知所有客户端在线用户变更
     */
    private void notifyOnlineUsersChangeInternal() {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送在线用户数量通知");
            return;
        }
        
        // 只发送简化版数据（仅数量）
        sendOnlineUserCount();
    }

    /**
     * 用户在线信息
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * 用户在线DTO（用于返回给前端）
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * 在线用户变更事件
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }

    //==================================
    //      WebSocket消息发送功能
    //==================================

    /**
     * 向所有客户端发送字典更新事件
     *
     * @param dictCode 字典编码
     */
    @Override
    public void broadcastDictChange(String dictCode) {
        DictEvent event = new DictEvent(dictCode);
        sendDictEvent(event);
    }

    /**
     * 发送字典事件消息
     *
     * @param event 字典事件
     */
    private void sendDictEvent(DictEvent event) {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送字典更新通知");
            return;
        }
        
        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("已发送字典事件通知, dictCode: {}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("发送字典事件失败", e);
        }
    }

    /**
     * 向特定用户发送系统消息
     * 
     * @param username 用户名
     * @param message 消息内容
     */
    @Override
    public void sendNotification(String username, Object message) {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送用户消息");
            return;
        }
        
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messageJson);
            log.info("已向用户[{}]发送消息", username);
        } catch (JsonProcessingException e) {
            log.error("向用户[{}]发送消息失败", username, e);
        }
    }
    
    /**
     * 发送广播消息给所有用户
     * 
     * @param message 消息内容
     */
    public void broadcastMessage(String message) {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送广播消息");
            return;
        }
        
        try {
            SystemMessage systemMessage = new SystemMessage("系统", message, System.currentTimeMillis());
            String messageJson = objectMapper.writeValueAsString(systemMessage);
            messagingTemplate.convertAndSend("/topic/public", messageJson);
            log.info("已发送广播消息: {}", message);
        } catch (JsonProcessingException e) {
            log.error("发送广播消息失败", e);
        }
    }
    
    /**
     * 系统消息对象
     */
    @Data
    public static class SystemMessage {
        private String sender;
        private String content;
        private long timestamp;
        
        public SystemMessage(String sender, String content, long timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
} 
