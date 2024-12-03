package com.youlai.boot.shared.auth.service.impl;

import com.youlai.boot.shared.auth.model.AuthTokenResponse;
import com.youlai.boot.shared.auth.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * JWT 令牌服务实现
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "redis-token")
@Service
public class RedisTokenService implements TokenService {

    /**
     * 生成令牌
     *
     * @param authentication 用户认证信息
     * @return
     */
    @Override
    public AuthTokenResponse generateToken(Authentication authentication) {
        return null;
    }

    /**
     * 解析令牌
     *
     * @param token JWT Token
     * @return
     */
    @Override
    public Authentication parseToken(String token) {
        return null;
    }

    /**
     * 验证令牌
     *
     * @param token JWT Token
     * @return
     */
    @Override
    public boolean validateToken(String token) {
        return false;
    }

    /**
     * 刷新令牌
     *
     * @param token 刷新令牌
     * @return
     */
    @Override
    public AuthTokenResponse refreshToken(String token) {
        return null;
    }
}
