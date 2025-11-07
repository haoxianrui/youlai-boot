package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * WebSocket 会话清理服务
 * 
 * 功能：
 * - 定时清理僵尸会话
 * - 监控会话状态
 * - 输出统计信息
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "websocket.session-cleanup",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WebSocketSessionCleanupService {

    private final WebSocketServiceImpl webSocketService;

    /**
     * 定时输出 WebSocket 会话统计信息
     * 
     * 每 5 分钟执行一次
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void logSessionStatistics() {
        try {
            int onlineUserCount = webSocketService.getOnlineUserCount();
            int totalSessionCount = webSocketService.getTotalSessionCount();

            log.info("📊 WebSocket 统计 - 在线用户数: {}, 活跃会话数: {}",
                    onlineUserCount, totalSessionCount);

            // 详细信息（仅在有用户在线时输出）
            if (onlineUserCount > 0) {
                var onlineUsers = webSocketService.getOnlineUsers();
                onlineUsers.forEach(user -> {
                    log.debug("  - 用户[{}]: {} 个会话", user.getUsername(), user.getSessionCount());
                });
            }
        } catch (Exception ex) {
            log.error("❌ 输出会话统计信息失败", ex);
        }
    }

    /**
     * 健康检查
     * 
     * 每 30 秒执行一次，用于监控服务状态
     */
    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void healthCheck() {
        try {
            int onlineUserCount = webSocketService.getOnlineUserCount();
            int sessionCount = webSocketService.getTotalSessionCount();

            // 异常检测：如果会话数远大于用户数，可能存在会话泄漏
            if (sessionCount > onlineUserCount * 10 && onlineUserCount > 0) {
                log.warn("⚠ 检测到异常：会话数({})远大于用户数({}×10)，可能存在会话泄漏",
                        sessionCount, onlineUserCount);
            }
        } catch (Exception ex) {
            log.error("❌ 健康检查失败", ex);
        }
    }

    /**
     * 手动触发在线用户数广播
     * 
     * 可用于系统启动后的初始化或手动刷新
     */
    public void triggerOnlineCountBroadcast() {
        try {
            webSocketService.notifyOnlineUsersChange();
            log.info("✓ 手动触发在线用户数广播成功");
        } catch (Exception ex) {
            log.error("❌ 手动触发在线用户数广播失败", ex);
        }
    }
}

