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
 * JWT 令牌服务实现
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "redis-token")
@Service
public class RedisTokenManager implements TokenManager {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenManager(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
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
        int accessTokenTimeToLive = securityProperties.getRedisToken().getAccessTokenTimeToLive();
        int refreshTokenTimeToLive = securityProperties.getRedisToken().getRefreshTokenTimeToLive();
        Boolean multiLogin = securityProperties.getRedisToken().getMultiLogin();

        String accessToken = generateToken(authentication, TokenKeyEnum.ACCESS_TOKEN_KEY, accessTokenTimeToLive, multiLogin);
        String refreshToken = generateToken(authentication, TokenKeyEnum.REFRESH_TOKEN_KEY, refreshTokenTimeToLive, multiLogin);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenTimeToLive)
                .build();
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
        Boolean hasKey = redisTemplate.hasKey(accessTokenKey);
        return hasKey != null && hasKey;
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

        int accessTokenExpiration = securityProperties.getRedisToken().getRefreshTokenTimeToLive();
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
            String oldToken = (String) redisTemplate.opsForHash().get("userId-token:"+tokenKeyEnum.getValue(), userDetails.getUserId().toString());
            if (StringUtils.isNotBlank(oldToken)) {
                redisTemplate.opsForHash().delete("userId-token:"+tokenKeyEnum.getValue(), userDetails.getUserId().toString());
                redisTemplate.delete(tokenKeyEnum.getValue() + oldToken);
            }
            redisTemplate.opsForHash().put("userId-token:"+tokenKeyEnum.getValue(), userDetails.getUserId().toString(), token);
            // 设置userId-token的过期时间
            redisTemplate.opsForHash().getOperations().expire("userId-token:"+tokenKeyEnum.getValue(), ttl, TimeUnit.SECONDS);
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
            String refreshToken = (String) redisTemplate.opsForHash().get("userId-token:"+TokenKeyEnum.REFRESH_TOKEN_KEY.getValue(), user.getId().toString());

            redisTemplate.delete(TokenKeyEnum.ACCESS_TOKEN_KEY.getValue() + token);
            redisTemplate.delete(TokenKeyEnum.REFRESH_TOKEN_KEY.getValue() + refreshToken);
            // 删除 userId-token 的hashmap
            redisTemplate.opsForHash().delete("userId-token:"+TokenKeyEnum.ACCESS_TOKEN_KEY.getValue(), userId.toString());
            redisTemplate.opsForHash().delete("userId-token:"+TokenKeyEnum.REFRESH_TOKEN_KEY.getValue(), userId.toString());
        }
    }
}
