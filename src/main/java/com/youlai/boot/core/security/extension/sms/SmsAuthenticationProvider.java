package com.youlai.boot.core.security.extension.sms;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.core.security.extension.WechatAuthenticationToken;
import com.youlai.boot.core.security.model.SysUserDetails;
import com.youlai.boot.system.model.dto.UserAuthInfo;
import com.youlai.boot.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


/**
 * 短信验证码认证 Provider
 *
 * @author Ray.Hao
 * @since 2.17.0
 */
@Slf4j
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final StringRedisTemplate redisTemplate;


    public SmsAuthenticationProvider(UserService userService, StringRedisTemplate redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 短信验证码认证逻辑，参考 Spring Security 认证密码校验流程
     *
     * @param authentication 认证对象
     * @return 认证后的 Authentication 对象
     * @throws AuthenticationException 认证异常
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String mobile = (String) authentication.getPrincipal();
        String verifyCode = (String) authentication.getCredentials();

        // 根据手机号获取用户信息
        UserAuthInfo userAuthInfo = userService.getUserAuthInfoByMobile(mobile);

        // 检查用户状态是否有效
        if (ObjectUtil.notEqual(userAuthInfo.getStatus(), 1)) {
            throw new DisabledException("用户已被禁用");
        }

        // 校验发送短信验证码的手机号是否与当前登录用户一致

        String cachedVerifyCode= redisTemplate.opsForValue().get(RedisConstants.SMS_LOGIN_VERIFY_CODE_PREFIX + mobile);

        if ( !StrUtil.equals(verifyCode, cachedVerifyCode)) {
            throw new CredentialsExpiredException("验证码错误");
        }

        // 构建认证后的用户详情信息
        SysUserDetails userDetails = new SysUserDetails(userAuthInfo);

        // 创建已认证的 WeChatAuthenticationToken
        return SmsAuthenticationToken.authenticated(
                userDetails,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
