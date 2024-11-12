package com.youlai.boot.shared.auth.service;

import com.youlai.boot.shared.auth.model.CaptchaResult;
import com.youlai.boot.shared.auth.model.LoginResult;
import com.youlai.boot.shared.auth.model.RefreshTokenRequest;

/**
 * 认证服务接口
 *
 * @author haoxr
 * @since 2.4.0
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    LoginResult login(String username, String password);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    CaptchaResult getCaptcha();

    /**
     * 刷新令牌
     *
     * @param request 刷新令牌请求参数
     * @return 登录结果
     */
    LoginResult refreshToken(RefreshTokenRequest request);
}
