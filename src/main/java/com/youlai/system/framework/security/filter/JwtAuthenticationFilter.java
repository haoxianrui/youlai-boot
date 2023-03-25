package com.youlai.system.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.ResponseUtils;
import com.youlai.system.framework.security.JwtTokenManager;
import com.youlai.system.framework.security.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 校验过滤器
 *
 * @author haoxr
 * @date 2022/10/1
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenManager tokenManager;

    public JwtAuthenticationFilter(JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityConstants.LOGIN_PATH.equals(request.getRequestURI())) {
            // 登录接口放行
            chain.doFilter(request, response);
        }else{
            String jwt = resolveToken(request);
            if (StrUtil.isNotBlank(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // 验证JWT
                    this.tokenManager.validateToken(jwt);

                    // JWT验证有效获取Authentication存入Security上下文
                    Authentication authentication = this.tokenManager.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    chain.doFilter(request, response);
                }catch (Exception e){
                    ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
                }
            }else{
                ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
            }
        }
    }

    /**
     * Get token from header.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
