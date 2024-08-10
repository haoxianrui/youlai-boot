package com.youlai.system.common.constant;

/**
 * 系统常量
 *
 * @author haoxr
 * @since 1.0.0
 */
public interface SystemConstants {

    /**
     * 根节点ID
     */
    Long ROOT_NODE_ID = 0L;

    /**
     * 系统默认密码
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 超级管理员角色编码
     */
    String ROOT_ROLE_CODE = "ROOT";

    /**
     * IP限流最大分钟数配置系统配置KEY
     */
    String CONFIG_IP_RATE_LIMIT_MINUTE_KEY = "IP_RATE_LIMIT_MINUTE";

    /**
     * IP限流次数配置系统配置KEY
     * 在最大分钟数内，允许访问的次数
     * @since 1.0.0
     */
    String CONFIG_IP_RATE_LIMIT_COUNT_KEY = "IP_RATE_LIMIT_COUNT";

}
