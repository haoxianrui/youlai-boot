package com.youlai.system.enums;

import com.youlai.system.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 日志类型枚举
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(enumAsRef = true)
@Getter
public enum LogTypeEnum implements IBaseEnum<Integer> {

    OPERATION(1, "操作日志"),
    LOGIN (2, "登录日志");

    private final Integer value;

    private final String label;

    LogTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}