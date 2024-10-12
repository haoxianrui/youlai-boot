package com.youlai.boot.core.security.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.youlai.boot.common.constant.JwtClaimConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.core.security.model.SysUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT Token 工具类
 *
 * @author Ray Hao
 * @since 2.6.0
 */
@Component
public class JwtUtils {

    private static StringRedisTemplate redisTemplate;

    @Autowired
    public JwtUtils(StringRedisTemplate redisTemplate) {
        JwtUtils.redisTemplate = redisTemplate;
    }


    /**
     * JWT 加解密使用的密钥
     */
    private static byte[] key;


    /**
     * JWT Token 的有效时间(单位:秒)
     */
    private static int ttl;


    @Value("${security.jwt.key}")
    public void setKey(String key) {
        JwtUtils.key = key.getBytes();
    }

    @Value("${security.jwt.ttl}")
    public void setTtl(Integer ttl) {
        JwtUtils.ttl = ttl;
    }

    /**
     * 生成 JWT Token
     *
     * @param authentication 用户认证信息
     * @return Token 字符串
     */
    public static String createToken(Authentication authentication) {

        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();

        Map<String, Object> payload = new HashMap<>();
        payload.put(JwtClaimConstants.USER_ID, userDetails.getUserId()); // 用户ID
        payload.put(JwtClaimConstants.DEPT_ID, userDetails.getDeptId()); // 部门ID
        payload.put(JwtClaimConstants.DATA_SCOPE, userDetails.getDataScope()); // 数据权限范围

        // claims 中添加角色信息
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        payload.put(JwtClaimConstants.AUTHORITIES, roles);

        Date now = new Date();
        payload.put(JWTPayload.ISSUED_AT, now);

        // 设置过期时间 -1 表示永不过期
        if (ttl != -1) {
            Date expiration = DateUtil.offsetSecond(now, ttl);
            payload.put(JWTPayload.EXPIRES_AT, expiration);
        }
        payload.put(JWTPayload.SUBJECT, authentication.getName());
        payload.put(JWTPayload.JWT_ID, IdUtil.simpleUUID());
        return JWTUtil.createToken(payload, key);
    }


    /**
     * 从 JWT Token 中解析 Authentication  用户认证信息
     *
     * @param payloads JWT 载体
     * @return 用户认证信息
     */
    public static UsernamePasswordAuthenticationToken getAuthentication(JSONObject payloads) {
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(payloads.getLong(JwtClaimConstants.USER_ID)); // 用户ID
        userDetails.setDeptId(payloads.getLong(JwtClaimConstants.DEPT_ID)); // 部门ID
        userDetails.setDataScope(payloads.getInt(JwtClaimConstants.DATA_SCOPE)); // 数据权限范围

        userDetails.setUsername(payloads.getStr(JWTPayload.SUBJECT)); // 用户名
        // 角色集合
        Set<SimpleGrantedAuthority> authorities = payloads.getJSONArray(JwtClaimConstants.AUTHORITIES)
                .stream()
                .map(authority -> new SimpleGrantedAuthority(Convert.toStr(authority)))
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /**
     * 将 Token 加入黑名单
     */
    public static void addTokenToBlacklist(String token) {
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
            JSONObject payloads = JWTUtil.parseToken(token).getPayloads();
            String jti = payloads.getStr(JWTPayload.JWT_ID);
            Long expiration = payloads.getLong(JWTPayload.EXPIRES_AT);

            if (expiration != null) {
                long currentTimeSeconds = System.currentTimeMillis() / 1000;
                if (expiration < currentTimeSeconds) {
                    // Token已过期，直接返回
                    return;
                }
                // 计算Token剩余时间，将其加入黑名单
                long ttl = expiration - currentTimeSeconds;
                redisTemplate.opsForValue().set(SecurityConstants.BLACKLIST_TOKEN_PREFIX + jti, null, ttl, TimeUnit.SECONDS);
            } else {
                // 永不过期的Token永久加入黑名单
                redisTemplate.opsForValue().set(SecurityConstants.BLACKLIST_TOKEN_PREFIX + jti, null);
            }
        }
    }


}
