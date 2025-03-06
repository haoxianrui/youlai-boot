package com.youlai.boot.common.enums;

import lombok.Getter;

/**
 * @Description TODO
 * @Author wangtao
 * @Date 2025/2/27 14:48
 */
@Getter
public enum TokenKeyEnum {
    ACCESS_TOKEN_KEY("access_token:"),
    REFRESH_TOKEN_KEY ("refresh_token:");

    private final String value;

    TokenKeyEnum(String value) {
        this.value = value;
    }
}
