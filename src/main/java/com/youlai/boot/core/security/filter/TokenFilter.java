package com.youlai.boot.core.security.filter;

import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.common.util.ResponseUtils;
import com.youlai.boot.core.security.manager.RedisTokenManager;
import com.youlai.boot.core.security.manager.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author wangtao
 * @since 2025/3/6 16:50
 */
public class TokenFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    public TokenFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
                // 去除 Bearer 前缀
                token = token.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
                // 校验 JWT Token ，包括验签和是否过期
                boolean isValidate = tokenManager.validateToken(token);
                if (!isValidate) {
                    ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
                    return;
                }
                // 将 Token 解析为 Authentication 对象，并设置到 Spring Security 上下文中
                Authentication authentication = tokenManager.parseToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
            return;
        }
        // Token有效或无Token时继续执行过滤链
        filterChain.doFilter(request, response);
    }
}
