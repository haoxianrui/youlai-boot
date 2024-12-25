package com.youlai.boot.common.constant;

/**
 * 安全模块常量
 *
 * @author Ray.Hao
 * @since 2023/11/24
 */
public interface SecurityConstants {

    /**
     * 验证码缓存前缀
     */
    String CAPTCHA_CODE_PREFIX = "captcha_code:";

    /**
     * 角色和权限缓存前缀
     */
    String ROLE_PERMS_PREFIX = "role_perms:";

    /**
     * 黑名单Token缓存前缀
     */
    String BLACKLIST_TOKEN_PREFIX = "token:blacklist:";

    /**
     * 登录路径
     */
    String LOGIN_PATH = "/api/v1/auth/login";

    /**
     * JWT Token 前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 角色前缀，用于区分 authorities 角色和权限， ROLE_* 角色 、没有前缀的是权限
     */
    String ROLE_PREFIX = "ROLE_";
}
