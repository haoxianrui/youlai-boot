package com.youlai.boot.common.enums;

/**
 * 消息类型枚举
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
public enum MessageTypeEnum {
    WEBSOCKET("webScoket", "websocket消息");

    private String value;

    private String label;

    MessageTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
