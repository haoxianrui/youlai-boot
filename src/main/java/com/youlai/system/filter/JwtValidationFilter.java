package com.youlai.system.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.security.util.JwtUtils;
import com.youlai.system.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT token 校验过滤器
 *
 * @author Ray Hao
 * @since 2023/9/13
 */
public class JwtValidationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    private final byte[] secretKey;

    public JwtValidationFilter(RedisTemplate<String, Object> redisTemplate, String secretKey) {
        this.redisTemplate = redisTemplate;
        this.secretKey = secretKey.getBytes();
    }


    /**
     * 从请求中获取 JWT Token，校验 JWT Token 是否合法
     * <p>
     * 如果合法则将 Authentication 设置到 Spring Security Context 上下文中
     * 如果不合法则清空 Spring Security Context 上下文，并直接返回响应
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
                token = token.substring(SecurityConstants.JWT_TOKEN_PREFIX.length()); // 去除 Bearer 前缀

                // 解析 Token
                JWT jwt = JWTUtil.parseToken(token);

                // 检查 Token 是否有效(验签 + 是否过期)
                boolean isValidate = jwt.setKey(secretKey).validate(0);
                if (!isValidate) {
                    ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
                    return;
                }

                // 检查 Token 是否已被加入黑名单(注销)
                JSONObject payloads = jwt.getPayloads();
                String jti = payloads.getStr(JWTPayload.JWT_ID);
                boolean isTokenBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(SecurityConstants.BLACKLIST_TOKEN_PREFIX + jti));
                if (isTokenBlacklisted) {
                    ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
                    return;
                }

                // Token 有效将其解析为 Authentication 对象，并设置到 Spring Security 上下文中
                Authentication authentication = JwtUtils.getAuthentication(payloads);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
            return;
        }
        // Token有效或无Token时继续执行过滤链
        filterChain.doFilter(request, response);
    }
}
