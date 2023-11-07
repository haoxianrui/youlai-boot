package com.youlai.system.service.impl;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.model.dto.CaptchaResult;
import com.youlai.system.model.dto.LoginResult;
import com.youlai.system.core.security.jwt.JwtTokenProvider;
import com.youlai.system.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author haoxr
 * @since 2.4.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @Override
    public LoginResult login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username.toLowerCase().trim(), password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String accessToken = jwtTokenProvider.createToken(authentication);
        return LoginResult.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    /**
     * 注销
     */
    @Override
    public void logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = jwtTokenProvider.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            Claims claims = jwtTokenProvider.getTokenClaims(token);
            String jti = claims.get("jti", String.class);
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                long ttl = expiration.getTime() - System.currentTimeMillis();
                redisTemplate.opsForValue().set(SecurityConstants.BLACK_TOKEN_CACHE_PREFIX + jti, null, ttl, TimeUnit.MILLISECONDS);
            } else {
                redisTemplate.opsForValue().set(SecurityConstants.BLACK_TOKEN_CACHE_PREFIX + jti, null);
            }
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    @Override
    public CaptchaResult getCaptcha() {
        
        MathGenerator mathGenerator=new MathGenerator(1);
        CircleCaptcha circleCaptcha =new CircleCaptcha(120,25,4,3);
        circleCaptcha.setGenerator(mathGenerator);
        String captchaCode = circleCaptcha.getCode(); // 验证码
        String captchaBase64 = circleCaptcha.getImageBase64Data(); // 验证码图片Base64

        // 验证码文本缓存至Redis，用于登录校验
        String verifyCodeKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(SecurityConstants.VERIFY_CODE_CACHE_PREFIX + verifyCodeKey, captchaCode,
                120, TimeUnit.SECONDS);

        return CaptchaResult.builder()
                .verifyCodeKey(verifyCodeKey)
                .verifyCodeBase64(captchaBase64)
                .build();
    }

}
