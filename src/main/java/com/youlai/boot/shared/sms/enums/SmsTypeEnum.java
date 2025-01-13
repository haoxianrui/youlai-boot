package com.youlai.boot.shared.sms.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 短信类型枚举
 */
@Getter
public enum SmsTypeEnum implements IBaseEnum<String> {
    REGISTER("register", "注册短信验证码"),
    LOGIN("login", "登录短信验证码"),
    RESET_PASSWORD("reset-password", "重置密码短信验证码");

    private final String value;
    private final String label;

    SmsTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
