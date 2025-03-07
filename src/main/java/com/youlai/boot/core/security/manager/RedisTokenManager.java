package com.youlai.boot.core.security.manager;

import cn.hutool.core.convert.Convert;
import com.youlai.boot.common.enums.TokenKeyEnum;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.core.security.model.AuthenticationToken;
import com.youlai.boot.core.security.model.OnlineUser;
import com.youlai.boot.core.security.model.SysUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis Token 管理器
 * <p>
 * 用于生成、解析、校验、刷新 JWT Token
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@ConditionalOnProperty(value = "security.auth.type", havingValue = "redis-token")
@Service
public class RedisTokenManager implements TokenManager {

    // 常量定义
    private static final String USER_SESSION_MAP = "user_sessions:%s"; // %s=token_type
    private static final String SESSION_KEY = "session:%s:%s"; // %s=token_type,token
    private static final String SESSION_QUEUE = "session_queue:%s"; // %s=user_id

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenManager(
            SecurityProperties securityProperties,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成令牌
     *
     * @param authentication 用户认证信息
     * @return
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        int accessTokenTtl = securityProperties.getAuth().getAccessTokenTtl();

        // 创建新会话
        String accessToken = createNewSession(authentication, );

        // 创建刷新令牌（独立控制）
        String refreshToken = createNewSession(authentication, TokenType.REFRESH,
                config.getRefreshTokenTtl(), 1); // 刷新令牌强制单设备

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenTtl)
                .build();
    }

    private String createNewSession(Authentication authentication,
                                    TokenType tokenType,
                                    int ttl,
                                    int maxSessions) {
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String token = UUID.randomUUID().toString();

        // 会话存储
        String sessionKey = keyGenerator.getSessionKey(tokenType, token);
        redisTemplate.opsForValue().set(sessionKey, buildOnlineUser(userDetails), ttl, TimeUnit.SECONDS);

        // 用户-会话映射
        String userMapKey = keyGenerator.getUserSessionMapKey(tokenType);
        redisTemplate.opsForHash().put(userMapKey, userId.toString(), token);
        redisTemplate.expire(userMapKey, ttl, TimeUnit.SECONDS);

        // 多设备控制
        enforceMaxSessions(userId, token, tokenType, maxSessions);

        return token;
    }

    private void enforceMaxSessions(Long userId, String currentToken, TokenType tokenType, int maxSessions) {
        if (maxSessions <= 0) return;

        String sessionQueueKey = keyGenerator.getSessionQueueKey(userId);
        long now = System.currentTimeMillis();

        // 使用ZSet维护会话队列
        redisTemplate.opsForZSet().add(sessionQueueKey, currentToken, now);
        redisTemplate.expire(sessionQueueKey, 7, TimeUnit.DAYS);

        // 移除超出数量的旧会话
        long excess = redisTemplate.opsForZSet().size(sessionQueueKey) - maxSessions;
        if (excess > 0) {
            Set<String> oldTokens = redisTemplate.opsForZSet().range(sessionQueueKey, 0, excess - 1);
            redisTemplate.opsForZSet().removeRange(sessionQueueKey, 0, excess - 1);

            // 吊销旧令牌
            oldTokens.forEach(oldToken -> {
                redisTemplate.delete(keyGenerator.getSessionKey(tokenType, oldToken));
                redisTemplate.opsForHash().delete(
                        keyGenerator.getUserSessionMapKey(tokenType),
                        userId.toString()
                );
            });
        }
    }


    /**
     * 解析令牌
     *
     * @param token JWT Token
     * @return
     */
    @Override
    public Authentication parseToken(String token) {
        String accessTokenKey = TokenKeyEnum.ACCESS_TOKEN_KEY.getValue() + token;
        OnlineUser user = (OnlineUser) redisTemplate.opsForValue().get(accessTokenKey);
        Set<SimpleGrantedAuthority> authorities = user.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(Convert.toStr(authority)))
                .collect(Collectors.toSet());
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setDeptId(user.getDeptId());
        userDetails.setDataScope(user.getDataScope());
        userDetails.setAuthorities(authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /**
     * 验证令牌
     *
     * @param token JWT Token
     * @return
     */
    @Override
    public boolean validateToken(String token) {
        String accessTokenKey = TokenKeyEnum.ACCESS_TOKEN_KEY.getValue() + token;
        return redisTemplate.hasKey(accessTokenKey);
    }

    /**
     * 刷新令牌
     *
     * @param token 刷新令牌
     * @return
     */
    @Override
    public AuthenticationToken refreshToken(String token) {
        String refreshTokenKey = TokenKeyEnum.REFRESH_TOKEN_KEY.getValue() + token;
        Authentication authentication = (Authentication) redisTemplate.opsForValue().get(refreshTokenKey);
        if (authentication == null) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        int accessTokenExpiration = securityProperties.getAuth().getRefreshTokenTtl();
        // 生成新的访问令牌
        String newAccessToken = generateToken(authentication, TokenKeyEnum.ACCESS_TOKEN_KEY, accessTokenExpiration, true);

        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .expiresIn(accessTokenExpiration)
                .build();
    }

    /**
     * 创建令牌
     *
     * @param authentication  认证信息
     * @param tokenKeyEnum    令牌类型
     * @param ttl             有效期
     * @param multiLogin      是否允许多点登录
     * @return
     */
    private String generateToken(Authentication authentication, TokenKeyEnum tokenKeyEnum, int ttl, boolean multiLogin) {
        // 获取用户信息
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        String token = UUID.randomUUID().toString();
        String tokenKey = tokenKeyEnum.getValue() + token;
        // 不允许多点登录，使用hashmap存储在线用户id和token
        if (!multiLogin) {
            // 查找当前用户id是否有token，有的话，说明已经登录了，就删除旧的token
            String oldToken = (String) redisTemplate.opsForHash().get("userId-token:" + tokenKeyEnum.getValue(), userDetails.getUserId().toString());
            if (StringUtils.isNotBlank(oldToken)) {
                redisTemplate.opsForHash().delete("userId-token:" + tokenKeyEnum.getValue(), userDetails.getUserId().toString());
                redisTemplate.delete(tokenKeyEnum.getValue() + oldToken);
            }
            redisTemplate.opsForHash().put("userId-token:" + tokenKeyEnum.getValue(), userDetails.getUserId().toString(), token);
            // 设置userId-token的过期时间
            redisTemplate.opsForHash().getOperations().expire("userId-token:" + tokenKeyEnum.getValue(), ttl, TimeUnit.SECONDS);
        }

        // 存储用户信息
        OnlineUser user = new OnlineUser();
        user.setId(userDetails.getUserId());
        user.setUsername(userDetails.getUsername());
        user.setDeptId(userDetails.getDeptId());
        user.setDataScope(userDetails.getDataScope());

        // claims 中添加角色信息
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        user.setAuthorities(roles);
        redisTemplate.opsForValue().set(tokenKey, user, ttl, TimeUnit.SECONDS);
        return token;
    }

    /**
     * 清除redis中的用户信息
     *
     * @param token Redis Token
     */
    @Override
    public void blacklistToken(String token) {
        /**
         * 根据token，删除当前用户的accessToken和refreshToken，以及 userId-token 的hashmap
         */
        OnlineUser user = (OnlineUser) redisTemplate.opsForValue().get(TokenKeyEnum.ACCESS_TOKEN_KEY.getValue() + token);
        if (!Objects.isNull(user)) {
            Long userId = user.getId();
            String refreshToken = (String) redisTemplate.opsForHash().get("userId-token:" + TokenKeyEnum.REFRESH_TOKEN_KEY.getValue(), user.getId().toString());

            redisTemplate.delete(TokenKeyEnum.ACCESS_TOKEN_KEY.getValue() + token);
            redisTemplate.delete(TokenKeyEnum.REFRESH_TOKEN_KEY.getValue() + refreshToken);
            // 删除 userId-token 的hashmap
            redisTemplate.opsForHash().delete("userId-token:" + TokenKeyEnum.ACCESS_TOKEN_KEY.getValue(), userId.toString());
            redisTemplate.opsForHash().delete("userId-token:" + TokenKeyEnum.REFRESH_TOKEN_KEY.getValue(), userId.toString());
        }
    }
}
