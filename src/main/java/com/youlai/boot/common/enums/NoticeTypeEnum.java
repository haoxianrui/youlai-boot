package com.youlai.boot.common.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通知类型枚举
 * 1-系统通知 0-系统消息
 *
 * @since 2024-9-1 17:33:06
 * @author Theo
 */
@Getter
@RequiredArgsConstructor
public enum NoticeTypeEnum implements IBaseEnum<Integer> {

    SYSTEM_NOTICE(1, "系统通知"),
    SYSTEM_MESSAGE (0, "系统消息");

    @Getter
    private Integer value;

    @Getter
    private String label;

    NoticeTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
