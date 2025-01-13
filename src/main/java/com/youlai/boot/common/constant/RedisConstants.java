package com.youlai.boot.common.constant;

/**
 * Redis Key常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {

    /**
     * 系统配置 Redis 键
     */
    String SYSTEM_CONFIG_KEY = "system:config";

    /**
     * IP 限流 Redis 键
     */
    String IP_RATE_LIMITER_KEY = "rate:limiter:ip:";

    /**
     * 防重复提交 Redis 键前缀
     */
    String RESUBMIT_LOCK_PREFIX = "lock:resubmit:";

    /**
     * 登录手机验证码 Redis 键前缀
     */
    String SMS_LOGIN_CODE_PREFIX= "code:sms:login:";

    /**
     * 绑定或更换手机号验证码 Redis 键前缀
     */
    String SMS_CHANGE_CODE_PREFIX = "code:sms:change:";

    /**
     * 绑定或更换邮箱验证码 Redis 键前缀
     */
    String EMAIL_CHANGE_CODE_PREFIX = "code:email:change:";
}
