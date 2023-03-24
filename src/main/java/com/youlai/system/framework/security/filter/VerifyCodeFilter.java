package com.youlai.system.framework.security.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlai.system.common.constant.CacheConstants;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 验证码校验过滤器
 *
 * @author haoxr
 * @date 2022/10/1
 */
public class VerifyCodeFilter extends OncePerRequestFilter {

    /**
     * 拦截路径
     */
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/auth/login", "POST");
    public static final String VERIFY_CODE = "verifyCode";
    public static final String VERIFY_CODE_KEY = "verifyCodeKey";

    RedisTemplate redisTemplate;

    public VerifyCodeFilter() {
        this.redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request)) {
            // 非登录接口放行
            chain.doFilter(request, response);
        } else {
            // 请求中的验证码
            String requestVerifyCode = request.getParameter(VERIFY_CODE);

            // 缓存中的验证码
            String verifyCodeKey = request.getParameter(VERIFY_CODE_KEY);
            Object cacheVerifyCode = redisTemplate.opsForValue().get(CacheConstants.VERIFY_CODE_CACHE_PREFIX + verifyCodeKey);
            if (cacheVerifyCode == null) {
                ResponseUtils.writeErrMsg(response, ResultCode.VERIFY_CODE_TIMEOUT);
            } else {
                // 验证码比对
                if (StrUtil.equals(requestVerifyCode, Convert.toStr(cacheVerifyCode))) {
                    chain.doFilter(request, response);
                } else {
                    ResponseUtils.writeErrMsg(response, ResultCode.VERIFY_CODE_ERROR);
                }
            }
        }
    }

}
