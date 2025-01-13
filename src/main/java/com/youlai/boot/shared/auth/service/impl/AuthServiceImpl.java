package com.youlai.boot.shared.auth.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.config.property.CaptchaProperties;
import com.youlai.boot.core.security.extension.WechatAuthenticationToken;
import com.youlai.boot.core.security.util.SecurityUtils;
import com.youlai.boot.shared.auth.enums.CaptchaTypeEnum;
import com.youlai.boot.core.security.model.AuthToken;
import com.youlai.boot.shared.auth.model.CaptchaResponse;
import com.youlai.boot.shared.auth.model.RefreshTokenRequest;
import com.youlai.boot.shared.auth.service.AuthService;
import com.youlai.boot.core.security.manager.TokenManager;
import com.youlai.boot.shared.sms.enums.SmsTypeEnum;
import com.youlai.boot.shared.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author haoxr
 * @since 2.4.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CodeGenerator codeGenerator;
    private final Font captchaFont;
    private final CaptchaProperties captchaProperties;
    private final TokenManager tokenManager;

    private final SmsService smsService;

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    @Override
    public AuthToken login(String username, String password) {
        // 1. 创建用于密码认证的令牌（未认证）
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username.trim(), password);

        // 2. 执行认证（认证中）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken authTokenResponse =
                tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authTokenResponse;
    }

    /**
     * 微信一键授权登录
     *
     * @param code 微信登录code
     * @return 访问令牌
     */
    @Override
    public AuthToken wechatLogin(String code) {
        // 1. 创建用户微信认证的令牌（未认证）
        WechatAuthenticationToken authenticationToken = new WechatAuthenticationToken(code);

        // 2. 执行认证（认证中）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken authTokenResponse = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authTokenResponse;
    }


    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    @Override
    public void sendLoginVerifyCode(String mobile) {

        // 随机生成4位验证码
        String verifyCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // 发送短信验证码
        smsService.sendSms(mobile, SmsTypeEnum.LOGIN, verifyCode);





    }

    /**
     * 注销
     */
    @Override
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
            // 将JWT令牌加入黑名单
            tokenManager.blacklistToken(token);
            // 清除Security上下文
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    @Override
    public CaptchaResponse getCaptcha() {

        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if (CaptchaTypeEnum.CIRCLE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.GIF.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else if (CaptchaTypeEnum.LINE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.SHEAR.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        }
        captcha.setGenerator(codeGenerator);
        captcha.setTextAlpha(captchaProperties.getTextAlpha());
        captcha.setFont(captchaFont);

        String captchaCode = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        // 验证码文本缓存至Redis，用于登录校验
        String captchaKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(SecurityConstants.CAPTCHA_CODE_PREFIX + captchaKey, captchaCode,
                captchaProperties.getExpireSeconds(), TimeUnit.SECONDS);

        return CaptchaResponse.builder()
                .captchaKey(captchaKey)
                .captchaBase64(imageBase64Data)
                .build();
    }

    /**
     * 刷新令牌
     *
     * @param request 刷新令牌请求参数
     * @return 新的访问令牌
     */
    @Override
    public AuthToken refreshToken(RefreshTokenRequest request) {
        // 验证刷新令牌

        String refreshToken = request.getRefreshToken();

        boolean isValidate = tokenManager.validateToken(refreshToken);

        if (!isValidate) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        return tokenManager.refreshToken(refreshToken);
    }


}
