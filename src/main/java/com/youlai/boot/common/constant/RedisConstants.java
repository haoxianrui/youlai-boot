package com.youlai.boot.common.constant;

/**
 * Redis Key常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {

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

    /**
     * 单个IP请求的最大每秒查询数（QPS）阈值Key
     */
    String IP_QPS_THRESHOLD_LIMIT_KEY = "IP_QPS_THRESHOLD_LIMIT";

    /**
     * 手机验证码缓存前缀
     */

    String MOBILE_VERIFICATION_CODE_PREFIX = "VERIFICATION_CODE:MOBILE:";


    /**
     * 邮箱验证码缓存前缀
     */
    String EMAIL_VERIFICATION_CODE_PREFIX = "VERIFICATION_CODE:EMAIL:";

}
