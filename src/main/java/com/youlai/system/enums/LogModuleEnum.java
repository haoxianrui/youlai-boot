package com.youlai.system.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 日志模块枚举
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(enumAsRef = true)
@Getter
public enum LogModuleEnum {

    /**
     * 登录
     */
    LOGIN("登录"),
    /**
     * 用户模块
     */
    USER("用户模块"),
    /**
     * 部门模块
     */
    DEPT("部门模块"),
    /**
     * 角色模块
     */
    ROLE("角色模块"),
    /**
     * 菜单模块
     */
    MENU("菜单模块"),
    /**
     * 字典模块
     */
    DICT("字典模块"),

    OTHER("其他")
    ;

    @JsonValue
    private final String moduleName;

    LogModuleEnum(String moduleName) {
        this.moduleName = moduleName;
    }
}