package com.youlai.boot.core.security.token;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.core.security.model.AuthenticationToken;
import com.youlai.boot.core.security.model.OnlineUser;
import com.youlai.boot.core.security.model.SysUserDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
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
@ConditionalOnProperty(value = "security.session.type", havingValue = "redis-token")
@Service
public class RedisTokenManager implements TokenManager {

    // 安全配置属性
    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenManager(SecurityProperties securityProperties,
                             RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        SysUserDetails user = (SysUserDetails) authentication.getPrincipal();
        int accessTtl = securityProperties.getSession().getAccessTokenTimeToLive();
        int refreshTtl = securityProperties.getSession().getRefreshTokenTimeToLive();

        // 生成随机令牌
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();

        // 构建用户在线信息（不包含密码）
        OnlineUser onlineUser = buildOnlineUser(user);

        // 将访问令牌与刷新令牌与用户信息分别存入 Redis，并设置过期时间
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, accessToken),
                onlineUser,
                accessTtl,
                TimeUnit.SECONDS
        );
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken),
                onlineUser,
                refreshTtl,
                TimeUnit.SECONDS
        );

        // 单设备登录控制，若不允许多设备登录，则通过用户ID映射保存当前最新的访问令牌
        Boolean allowMultiLogin = securityProperties.getSession().getRedisToken().getAllowMultiLogin();
        if (!allowMultiLogin) {
            Long userId = user.getUserId();
            String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
            // 获取当前用户已有的访问令牌
            String oldAccessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
            if (oldAccessToken != null) {
                // 删除旧的访问令牌对应的用户信息缓存
                redisTemplate.delete(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, oldAccessToken));
            }
            // 更新用户与访问令牌的映射
            redisTemplate.opsForValue().set(userAccessKey, accessToken, accessTtl, TimeUnit.SECONDS);
        }
        // 同时存储用户与刷新令牌的映射，便于后续刷新和踢出旧会话
        String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, user.getUserId());
        redisTemplate.opsForValue().set(userRefreshKey, refreshToken, refreshTtl, TimeUnit.SECONDS);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTtl)
                .build();
    }

    /**
     * 根据 token 解析用户信息
     *
     * @param token JWT Token
     * @return
     */
    @Override
    public Authentication parseToken(String token) {
        // 根据访问令牌从 Redis 中获取在线用户信息
        String tokenUserCacheKey = StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(tokenUserCacheKey);

        if (onlineUser == null) return null;

        // 构建用户权限集合
        Set<SimpleGrantedAuthority> authorities = onlineUser.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        // 构建用户详情对象
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(onlineUser.getUserId());
        userDetails.setUsername(onlineUser.getUsername());
        userDetails.setDeptId(onlineUser.getDeptId());
        userDetails.setDataScope(onlineUser.getDataScope());
        userDetails.setAuthorities(authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token  访问令牌
     * @return
     */
    @Override
    public boolean validateToken(String token) {
        String tokenKey = StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
        return redisTemplate.hasKey(tokenKey);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        // 根据刷新令牌获取在线用户信息
        String refreshKey = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(refreshKey);

        if (onlineUser == null) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 获取当前用户的旧访问令牌（如果存在）
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId());
        String oldAccessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
        if (oldAccessToken != null) {
            // 删除旧的访问令牌记录
            redisTemplate.delete(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, oldAccessToken));
        }

        // 生成新访问令牌
        String newAccessToken = IdUtil.fastSimpleUUID();
        int accessTtl = securityProperties.getSession().getAccessTokenTimeToLive();
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, newAccessToken),
                onlineUser,
                accessTtl,
                TimeUnit.SECONDS
        );

        // 更新用户与访问令牌的映射（若单设备登录，则更新映射以踢出旧会话）
        redisTemplate.opsForValue().set(userAccessKey, newAccessToken, accessTtl, TimeUnit.SECONDS);

        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTtl)
                .build();
    }


    /**
     * 将 Token 加入黑名单
     *
     * @param token 访问令牌
     */
    @Override
    public void blacklistToken(String token) {
        // 删除访问令牌对应的在线用户信息缓存
        String accessKey = StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(accessKey);

        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();

            // 删除访问令牌缓存和用户与访问令牌的映射
            redisTemplate.delete(accessKey);
            redisTemplate.delete(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId));

            // 删除用户与刷新令牌的映射，以及刷新令牌对应的缓存
            String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, userId);
            String refreshToken = (String) redisTemplate.opsForValue().get(userRefreshKey);
            if (refreshToken != null) {
                redisTemplate.delete(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
                redisTemplate.delete(userRefreshKey);
            }
        }
    }

    /**
     * 构建 OnlineUser 对象
     */
    private OnlineUser buildOnlineUser(SysUserDetails user) {
        Long userId = user.getUserId();
        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return new OnlineUser(
                userId,
                user.getUsername(),
                user.getDeptId(),
                user.getDataScope(),
                roles
        );
    }
}
