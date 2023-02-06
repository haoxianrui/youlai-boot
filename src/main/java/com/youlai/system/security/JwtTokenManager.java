package com.youlai.system.security;

import cn.hutool.core.convert.Convert;
import com.youlai.system.security.userdetails.SysUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
 * @date 2022/10/22
 */
@Component
public class JwtTokenManager {

    /**
     * secret key.
     */
    @Value("${auth.token.secret_key}")
    private String secretKey;

    /**
     * Token validity time(seconds).
     */
    @Value("${auth.token.token_validity}")
    private long tokenValidity;

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

        long now = System.currentTimeMillis();

        Date validity;

        validity = new Date(now + tokenValidity * 1000L);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
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
        redisTemplate.opsForValue().set("USER_PERMS:" + userDetails.getUserId(), perms);

        return Jwts.builder().setClaims(claims).setExpiration(validity)
                .signWith( Keys.hmacShaKeyFor(this.getSecretKeyBytes()),SignatureAlgorithm.HS256).compact();
    }

    /**
     * 获取认证信息
     */
    public Authentication getAuthentication(String token) {
        if (jwtParser == null) {
            jwtParser = Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build();
        }
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        SysUserDetails principal = new SysUserDetails();
        principal.setUserId(Convert.toLong(claims.get("userId")));
        principal.setUsername(Convert.toStr(claims.get("username")));
        principal.setDeptId(Convert.toLong(claims.get("deptId")));
        principal.setDataScope(Convert.toInt(claims.get("dataScope")));

        List<SimpleGrantedAuthority> authorities = ((ArrayList<String>) claims.get("authorities"))
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 验证token
     */
    public void validateToken(String token) {
        if (jwtParser == null) {
            jwtParser = Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build();
        }
        jwtParser.parseClaimsJws(token);
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
