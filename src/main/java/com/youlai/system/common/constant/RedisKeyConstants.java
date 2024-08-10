package com.youlai.system.common.constant;

/**
 * Redis Key常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisKeyConstants {

    /**
     * 系统配置Redis-key
     */
    String SYSTEM_CONFIG_KEY = "system:config";

    /**
     * IP限流Redis-key
     */
    String IP_RATE_LIMITER_KEY = "ip:rate:limiter:";

    /**
     * 防重复提交Redis-key
     */
    String RESUBMIT_LOCK_PREFIX = "resubmit:lock:";
}
