package com.youlai.system.framework.security.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlai.system.common.constant.CacheConstants;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.ResponseUtils;
import com.youlai.system.framework.security.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 验证码校验过滤器
 *
 * @author haoxr
 * @date 2022/10/1
 */
public class VerifyCodeFilter extends OncePerRequestFilter {

    public static final String VERIFY_CODE = "verifyCode";
    public static final String VERIFY_CODE_KEY = "verifyCodeKey";

    RedisTemplate redisTemplate;

    public VerifyCodeFilter() {
        this.redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 检验登录接口的验证码
        if (SecurityConstants.LOGIN_PATH.equals(request.getRequestURI())) {
            // 请求中的验证码
            String requestVerifyCode = request.getParameter(VERIFY_CODE);

            // TODO 兼容 2.0.0 无验证码版本，后续移除
            if (StrUtil.isBlank(requestVerifyCode)) {
                // 非登录接口放行
                chain.doFilter(request, response);
                return;
            }
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
        } else {
            // 非登录接口放行
            chain.doFilter(request, response);
        }
    }

}
