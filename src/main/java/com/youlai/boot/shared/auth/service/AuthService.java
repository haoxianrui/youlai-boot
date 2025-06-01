package com.youlai.boot.shared.auth.service;

import com.youlai.boot.shared.auth.model.CaptchaInfo;
import com.youlai.boot.core.security.model.AuthenticationToken;
import com.youlai.boot.shared.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.youlai.boot.shared.auth.model.dto.WxMiniAppPhoneLoginDTO;

/**
 * 认证服务接口
 *
 * @author Ray.Hao
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
    AuthenticationToken login(String username, String password);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    CaptchaInfo getCaptcha();

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthenticationToken refreshToken(String refreshToken);

    /**
     * 微信小程序登录
     *
     * @param code 微信登录code
     * @return 登录结果
     */
    AuthenticationToken loginByWechat(String code);

    /**
     * 微信小程序Code登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthenticationToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO);

    /**
     * 微信小程序手机号登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthenticationToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO);

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    void sendSmsLoginCode(String mobile);

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 登录结果
     */
    AuthenticationToken loginBySms(String mobile, String code);
}
