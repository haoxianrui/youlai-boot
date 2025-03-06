package com.youlai.boot.core.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码校验异常
 *
 * @author Ray.Hao
 * @since 2025/3/1
 */
public class CaptchaValidationException extends AuthenticationException {
    public CaptchaValidationException(String msg) {
        super(msg);
    }
}