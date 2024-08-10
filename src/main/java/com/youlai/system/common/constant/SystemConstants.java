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
     * 单个IP请求的最大每秒查询数（QPS）阈值Key
     */
    String IP_QPS_THRESHOLD_LIMIT_KEY = "IP_QPS_THRESHOLD_LIMIT";

}
