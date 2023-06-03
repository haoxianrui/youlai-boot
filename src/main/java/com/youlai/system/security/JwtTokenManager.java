package com.youlai.system.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.security.userdetails.SysUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT token manager
 *
 * @author haoxr
 * @since 2022/10/22
 */
@Component
public class JwtTokenManager {

    /**
     * token加密密钥
     */
    @Value("${auth.token.secret_key}")
    private String secretKey;

    /**
     * token有效期(单位:秒)
     */
    @Value("${auth.token.ttl}")
    private Long tokenTtl;

    /**
     * secret key byte array.
     */
    private byte[] secretKeyBytes;

    private JwtParser jwtParser;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Create token.
     *
     * @param authentication auth info
     * @return token
     */
    public String createToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        claims.put("jti",IdUtil.fastSimpleUUID());
        claims.put("userId", userDetails.getUserId());
        claims.put("username", claims.getSubject());
        claims.put("deptId", userDetails.getDeptId());
        claims.put("dataScope", userDetails.getDataScope());

        // 角色放入JWT的claims
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toSet());
        claims.put("authorities", roles);

        // 权限数据多放入Redis
        Set<String> perms = userDetails.getPerms();
        redisTemplate.opsForValue().set(SecurityConstants.USER_PERMS_CACHE_PREFIX + userDetails.getUserId(), perms);

        // 过期时间
        Date expirationTime = new Date(System.currentTimeMillis() + tokenTtl * 1000L);
        return Jwts.builder()
                //.setId(IdUtil.fastSimpleUUID()) TODO 设置jti无效
                .setClaims(claims)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(this.getSecretKeyBytes()), SignatureAlgorithm.HS256).compact();
    }

    /**
     * 获取认证信息
     */
    public Authentication getAuthentication( Claims claims) {
        SysUserDetails principal = new SysUserDetails();
        principal.setUserId(Convert.toLong(claims.get("userId"))); // 用户ID
        principal.setUsername(Convert.toStr(claims.get("username"))); // 用户名
        principal.setDeptId(Convert.toLong(claims.get("deptId"))); // 部门ID
        principal.setDataScope(Convert.toInt(claims.get("dataScope"))); // 数据权限

        List<SimpleGrantedAuthority> authorities = ((ArrayList<String>) claims.get("authorities"))
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 解析 & 验证 token
     */
    public Claims parseAndValidateToken(String token) {
        // 解析成功说明JWT有效
        Claims claims = this.getTokenClaims(token);
        // 验证JWT 是否在黑名单(注销场景会存入黑名单)
        Boolean isBlack = redisTemplate.hasKey(SecurityConstants.BLACK_TOKEN_CACHE_PREFIX + claims.get("jti"));

        if (isBlack) {
            throw new RuntimeException("token 已被禁用");
        }
        return claims;
    }

    public byte[] getSecretKeyBytes() {
        if (secretKeyBytes == null) {
            try {
                secretKeyBytes = Decoders.BASE64.decode(secretKey);
            } catch (DecodingException e) {
                secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            }
        }
        return secretKeyBytes;
    }


    /**
     * get token claims
     */
    public Claims getTokenClaims(String token) {
        if (jwtParser == null) {
            jwtParser = Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build();
        }
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims;
    }
}