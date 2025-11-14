package com.youlai.boot.platform.websocket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.dto.DictEventDTO;
import com.youlai.boot.platform.websocket.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket 服务实现类
 * 
 * 核心功能：
 * - 用户在线状态管理（支持多设备登录）
 * - 消息推送（广播、点对点）
 * - 字典变更通知
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    // ==================== 在线用户管理 ====================
    
    /**
     * 用户在线会话映射表
     * Key: 用户名
     * Value: 该用户的所有会话 ID 集合（支持多设备登录）
     */
    private final Map<String, Set<String>> userSessionsMap = new ConcurrentHashMap<>();

    /**
     * 会话详情映射表
     * Key: 会话 ID
     * Value: 会话详细信息
     */
    private final Map<String, SessionInfo> sessionDetailsMap = new ConcurrentHashMap<>();

    // ==================== 依赖注入 ====================
    
    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 延迟注入 SimpMessagingTemplate，避免循环依赖
     */
    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("✓ WebSocket 消息模板已初始化");
    }

    // ==================== 用户在线状态管理 ====================

    /**
     * 处理用户连接事件
     *
     * @param username  用户名
     * @param sessionId WebSocket 会话 ID
     */
    @Override
    public void userConnected(String username, String sessionId) {
        if (username == null || username.isEmpty()) {
            log.warn("用户连接失败：用户名为空");
            return;
        }

        if (sessionId == null || sessionId.isEmpty()) {
            log.warn("用户[{}]连接失败：会话 ID 为空", username);
            return;
        }

        // 添加会话到用户的会话集合中（支持多设备登录）
        userSessionsMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);

        // 保存会话详情
        SessionInfo sessionInfo = new SessionInfo(username, sessionId, System.currentTimeMillis());
        sessionDetailsMap.put(sessionId, sessionInfo);

        int sessionCount = userSessionsMap.get(username).size();
        int totalOnlineUsers = userSessionsMap.size();

        log.info("✓ 用户[{}]会话[{}]上线（该用户共 {} 个会话，系统总在线用户数：{}）",
                username, sessionId, sessionCount, totalOnlineUsers);

        // 广播在线用户数变更
        broadcastOnlineUserCount();
    }

    /**
     * 处理用户断开连接事件
     *
     * @param username 用户名
     */
    @Override
    public void userDisconnected(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }

        // 获取该用户的所有会话
        Set<String> sessions = userSessionsMap.get(username);
        if (sessions == null || sessions.isEmpty()) {
            log.warn("用户[{}]下线：未找到会话记录", username);
            return;
        }

        // 移除所有会话详情（通常一次只断开一个会话，但这里做全量清理）
        sessions.forEach(sessionDetailsMap::remove);

        // 移除用户的会话记录
        userSessionsMap.remove(username);

        int totalOnlineUsers = userSessionsMap.size();
        log.info("✓ 用户[{}]下线（系统总在线用户数：{}）", username, totalOnlineUsers);

        // 广播在线用户数变更
        broadcastOnlineUserCount();
    }

    /**
     * 移除指定会话（单个设备下线）
     *
     * @param sessionId 会话 ID
     */
    public void removeSession(String sessionId) {
        SessionInfo sessionInfo = sessionDetailsMap.remove(sessionId);
        if (sessionInfo == null) {
            return;
        }

        String username = sessionInfo.getUsername();
        Set<String> sessions = userSessionsMap.get(username);

        if (sessions != null) {
            sessions.remove(sessionId);

            // 如果该用户没有其他会话了，移除用户记录
            if (sessions.isEmpty()) {
                userSessionsMap.remove(username);
                log.info("✓ 用户[{}]最后一个会话[{}]下线", username, sessionId);
            } else {
                log.info("✓ 用户[{}]会话[{}]下线（还剩 {} 个会话）", 
                        username, sessionId, sessions.size());
            }

            // 广播在线用户数变更
            broadcastOnlineUserCount();
        }
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户信息列表
     */
    public List<OnlineUserDTO> getOnlineUsers() {
        return userSessionsMap.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    Set<String> sessions = entry.getValue();

                    // 获取该用户最早的登录时间
                    long earliestLoginTime = sessions.stream()
                            .map(sessionDetailsMap::get)
                            .filter(info -> info != null)
                            .mapToLong(SessionInfo::getConnectTime)
                            .min()
                            .orElse(System.currentTimeMillis());

                    return new OnlineUserDTO(username, sessions.size(), earliestLoginTime);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数（不是会话数）
     */
    public int getOnlineUserCount() {
        return userSessionsMap.size();
    }

    /**
     * 获取在线会话总数
     *
     * @return 所有在线会话的总数
     */
    public int getTotalSessionCount() {
        return sessionDetailsMap.size();
    }

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return 是否在线
     */
    public boolean isUserOnline(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 获取指定用户的会话数量
     *
     * @param username 用户名
     * @return 会话数量
     */
    public int getUserSessionCount(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 手动触发在线用户数量广播
     * 
     * 供外部服务（如定时任务）调用
     */
    public void notifyOnlineUsersChange() {
        log.info("手动触发在线用户数量通知，当前在线用户数：{}", getOnlineUserCount());
        broadcastOnlineUserCount();
    }

    /**
     * 广播在线用户数量变更（内部方法）
     */
    private void broadcastOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送在线用户数量");
            return;
        }

        try {
            int count = getOnlineUserCount();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("✓ 已广播在线用户数量: {}", count);
        } catch (Exception e) {
            log.error("广播在线用户数量失败", e);
        }
    }

    // ==================== 消息推送功能 ====================

    /**
     * 向所有客户端广播字典更新事件
     *
     * @param dictCode 字典编码
     */
    @Override
    public void broadcastDictChange(String dictCode) {
        if (dictCode == null || dictCode.isEmpty()) {
            log.warn("字典编码为空，跳过广播");
            return;
        }

        DictEventDTO event = new DictEventDTO(dictCode);
        sendDictChangeEvent(event);
    }

    /**
     * 发送字典变更事件
     *
     * @param event 字典事件
     */
    private void sendDictChangeEvent(DictEventDTO event) {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送字典更新通知");
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("✓ 已广播字典变更通知: dictCode={}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("字典事件序列化失败: dictCode={}", event.getDictCode(), e);
        } catch (Exception e) {
            log.error("发送字典变更通知失败: dictCode={}", event.getDictCode(), e);
        }
    }

    /**
     * 向特定用户发送通知消息
     *
     * @param username 目标用户名
     * @param message  消息内容
     */
    @Override
    public void sendNotification(String username, Object message) {
        if (username == null || username.isEmpty()) {
            log.warn("用户名为空，无法发送通知");
            return;
        }

        if (message == null) {
            log.warn("消息内容为空，无法发送给用户[{}]", username);
            return;
        }

        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送用户消息");
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messageJson);
            log.info("✓ 已向用户[{}]发送通知", username);
        } catch (JsonProcessingException e) {
            log.error("消息序列化失败: username={}", username, e);
        } catch (Exception e) {
            log.error("向用户[{}]发送通知失败", username, e);
        }
    }

    /**
     * 广播系统消息给所有用户
     *
     * @param message 消息内容
     */
    public void broadcastSystemMessage(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("消息内容为空，无法广播");
            return;
        }

        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送广播消息");
            return;
        }

        try {
            SystemMessage systemMessage = new SystemMessage(
                    "系统通知",
                    message,
                    System.currentTimeMillis()
            );
            String messageJson = objectMapper.writeValueAsString(systemMessage);
            messagingTemplate.convertAndSend("/topic/public", messageJson);
            log.info("✓ 已广播系统消息: {}", message);
        } catch (JsonProcessingException e) {
            log.error("系统消息序列化失败", e);
        } catch (Exception e) {
            log.error("广播系统消息失败", e);
        }
    }

    // ==================== 内部数据类 ====================

    /**
     * 会话信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SessionInfo {
        /** 用户名 */
        private String username;
        /** 会话 ID */
        private String sessionId;
        /** 连接时间戳 */
        private long connectTime;
    }

    /**
     * 在线用户 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OnlineUserDTO {
        /** 用户名 */
        private String username;
        /** 会话数量 */
        private int sessionCount;
        /** 首次登录时间 */
        private long loginTime;
    }

    /**
     * 系统消息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemMessage {
        /** 发送者 */
        private String sender;
        /** 消息内容 */
        private String content;
        /** 时间戳 */
        private long timestamp;
    }
}
