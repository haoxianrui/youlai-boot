package com.youlai.boot.common.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通知方式枚举
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
@Getter
@RequiredArgsConstructor
public enum NoticeWayEnum implements IBaseEnum<String> {
    /**
     * 通知方式
     */
    WEBSOCKET("webSocket", "发送websocket消息");

    @Getter
    private String value;

    @Getter
    private String label;

    NoticeWayEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
