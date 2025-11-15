package com.youlai.boot.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket消息服务
 *
 * @author Ray
 * @since 3.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 字典事件类型
     */
    public enum DictEventType {
        /**
         * 字典更新
         */
        DICT_UPDATED,
        
        /**
         * 字典删除
         */
        DICT_DELETED
    }

    /**
     * 字典事件消息
     */
    public static class DictEvent {
        /**
         * 事件类型
         */
        private String type;
        
        /**
         * 字典编码
         */
        private String dictCode;
        
        /**
         * 时间戳
         */
        private long timestamp;

        public DictEvent(DictEventType type, String dictCode) {
            this.type = type.name();
            this.dictCode = dictCode;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDictCode() {
            return dictCode;
        }

        public void setDictCode(String dictCode) {
            this.dictCode = dictCode;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * 向所有客户端发送字典更新事件
     *
     * @param dictCode 字典编码
     */
    public void sendDictUpdatedEvent(String dictCode) {
        DictEvent event = new DictEvent(DictEventType.DICT_UPDATED, dictCode);
        sendDictEvent(event);
    }

    /**
     * 向所有客户端发送字典删除事件
     *
     * @param dictCode 字典编码
     */
    public void sendDictDeletedEvent(String dictCode) {
        DictEvent event = new DictEvent(DictEventType.DICT_DELETED, dictCode);
        sendDictEvent(event);
    }

    /**
     * 发送字典事件消息
     *
     * @param event 字典事件
     */
    private void sendDictEvent(DictEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("Sent dict event to clients: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to send dict event", e);
        }
    }
} 