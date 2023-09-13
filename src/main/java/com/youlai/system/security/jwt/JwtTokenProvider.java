package com.youlai.system.security.jwt;

import cn.hutool.core.convert.Convert;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.security.model.SysUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  JWT token 管理器
 *
 * @author haoxr
 * @since 2023/9/13
 */

@Component
public class JwtTokenProvider {

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${jwt.secret-key:123456}")
    private String secretKey;

    @Value("${jwt.expiration:7200}")
    private int expiration;

    private byte[] secretKeyBytes;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 创建Token
     *
     * @param authentication
     * @return
     */
    public String createToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        claims.put("userId", userDetails.getUserId());
        claims.put("username", claims.getSubject());
        claims.put("deptId", userDetails.getDeptId());
        claims.put("dataScope", userDetails.getDataScope());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        claims.put("authorities", roles);

        // 权限数据多放入Redis
        Set<String> perms = userDetails.getPerms();
        redisTemplate.opsForValue().set(SecurityConstants.USER_PERMS_CACHE_PREFIX + userDetails.getUserId(), perms);


        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expiration * 1000L);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(getSecretKeyBytes()), SignatureAlgorithm.HS256).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = this.getTokenClaims(token);

        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(Convert.toLong(claims.get("userId"))); // 用户ID
        userDetails.setUsername(Convert.toStr(claims.get("username"))); // 用户名
        userDetails.setDeptId(Convert.toLong(claims.get("deptId"))); // 部门ID
        userDetails.setDataScope(Convert.toInt(claims.get("dataScope"))); // 数据权限范围

        List<SimpleGrantedAuthority> authorities = ((ArrayList<String>) claims.get("authorities"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSecretKeyBytes()).build().parseClaimsJws(token);
        return true;
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }


    public Claims getTokenClaims(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build().parseClaimsJws(token).getBody();
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


}
