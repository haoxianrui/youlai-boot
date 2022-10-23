/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youlai.system.security.jwt;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * JWT token manager.
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
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toSet());
        Set<String> authorities = userDetails.getPerms();
        authorities.addAll(roles);
        redisTemplate.opsForValue().set("USER_PERMS:" + userDetails.getUserId(), authorities);
        return Jwts.builder().setClaims(claims).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, Keys.hmacShaKeyFor(this.getSecretKeyBytes())).compact();
    }

    /**
     * Create token.
     *
     * @param userName auth info
     * @return token
     */
    public String createToken(String userName) {

        long now = System.currentTimeMillis();

        Date validity;

        validity = new Date(now + tokenValidity * 1000L);

        Claims claims = Jwts.claims().setSubject(userName);

        return Jwts.builder().setClaims(claims).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, Keys.hmacShaKeyFor(this.getSecretKeyBytes())).compact();
    }

    /**
     * Get auth Info.
     *
     * @param token token
     * @return auth info
     */
    public Authentication getAuthentication(String token) {
        if (jwtParser == null) {
            jwtParser = Jwts.parserBuilder().setSigningKey(this.getSecretKeyBytes()).build();
        }
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        List<GrantedAuthority> authorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

        SysUserDetails principal = new SysUserDetails();
        principal.setUserId(Convert.toLong(claims.get("userId")));
        principal.setUsername(Convert.toStr(claims.get("username")));

        // 权限数据过多放置在redis
        Set<String> perms = (Set<String>) redisTemplate.opsForValue().get("USER_PERMS:" + claims.get("userId"));
        if (CollectionUtil.isNotEmpty(perms)) {
            List<GrantedAuthority> permAuthorities = perms.stream()
                    .map(perm -> new SimpleGrantedAuthority(perm))
                    .collect(Collectors.toList());
            authorities.addAll(permAuthorities);
        }
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * validate token.
     *
     * @param token token
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
