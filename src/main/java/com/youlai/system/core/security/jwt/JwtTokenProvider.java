package com.youlai.system.core.security.jwt;

import cn.hutool.core.convert.Convert;
import com.youlai.system.common.constant.JwtClaimConstants;
import com.youlai.system.core.security.model.SysUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT token 工具类
 * <p>
 * 用于生成/校验/解析 JWT Token
 *
 * @author haoxr
 * @since 2023/9/13
 */
@Component
public class JwtTokenProvider {

    /**
     * 签名密钥，用于签名 Access Token
     */
    @Value("${jwt.secret-key:123456}")
    private String secretKey;

    @Value("${jwt.expiration:7200}")
    private int expiration;

    /**
     * Base64 编码后的签名密钥，用于校验/解析 Access Token
     */
    private byte[] secretKeyBytes;


    /**
     * 初始化方法
     * <p>
     * 对签名密钥进行 Base64 编码
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 创建Token
     * <p>
     * 认证成功后的用户信息会被封装到 Authentication 对象中，然后通过 JwtTokenProvider#createToken(Authentication) 方法创建 Token 字符串
     *
     * @param authentication 用户认证信息
     * @return Token 字符串
     */
    public String createToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        claims.put(JwtClaimConstants.USER_ID, userDetails.getUserId()); // 用户ID
        claims.put(JwtClaimConstants.USERNAME, claims.getSubject()); // 用户名
        claims.put(JwtClaimConstants.DEPT_ID, userDetails.getDeptId()); // 部门ID
        claims.put(JwtClaimConstants.DATA_SCOPE, userDetails.getDataScope()); // 数据权限范围

        // claims 中添加角色信息
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        claims.put(JwtClaimConstants.AUTHORITIES, roles);

        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expiration * 1000L);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(getSecretKeyBytes()), SignatureAlgorithm.HS256).compact();
    }


    /**
     * 根据给定的令牌解析出用户认证信息
     *
     * @param token JWT Token
     * @return 用户认证信息
     */
    public Authentication getAuthentication(String token) {
        Claims claims = this.getTokenClaims(token);

        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(Convert.toLong(claims.get(JwtClaimConstants.USER_ID))); // 用户ID
        userDetails.setUsername(Convert.toStr(claims.get(JwtClaimConstants.USERNAME))); // 用户名
        userDetails.setDeptId(Convert.toLong(claims.get(JwtClaimConstants.DEPT_ID))); // 部门ID
        userDetails.setDataScope(Convert.toInt(claims.get(JwtClaimConstants.DATA_SCOPE))); // 数据权限范围

        // 角色集合
        Set<SimpleGrantedAuthority> authorities = ((Set<String>) claims.get(JwtClaimConstants.AUTHORITIES))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }


    /**
     * 从请求头中获取Token
     *
     * @param req 请求对象
     * @return Token 字符串
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 校验Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSecretKeyBytes()).build().parseClaimsJws(token);
        return true;
    }

    /**
     * 获取Token中的用户名
     *
     * @param token Token
     * @return
     */
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 获取Token的Claims，claims中包含了用户的基本信息
     *
     * @param token
     * @return
     */
    public Claims getTokenClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build().parseClaimsJws(token).getBody();
    }

    /**
     * 获取签名密钥的字节数组
     *
     * @return 签名密钥的字节数组
     */
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


}
