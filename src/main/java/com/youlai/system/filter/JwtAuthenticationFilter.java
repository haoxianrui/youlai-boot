package com.youlai.system.filter;

import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.security.jwt.JwtTokenManager;
import com.youlai.system.util.ResponseUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt auth token filter.
 *
 * @author haoxr
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenManager tokenManager;

    public JwtAuthenticationFilter(JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if(HttpMethod.OPTIONS.matches(request.getMethod()) ){
            chain.doFilter(request, response);
            return;
        }
        String jwt = resolveToken(request);
        if (StrUtil.isNotBlank(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                this.tokenManager.validateToken(jwt);
                Authentication authentication = this.tokenManager.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            }catch (Exception e){
                ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID_OR_EXPIRED);
            }
        }else{
            ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID_OR_EXPIRED);
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
