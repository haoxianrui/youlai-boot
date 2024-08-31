package com.youlai.boot.core.filter;

import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.common.util.IPUtils;
import com.youlai.boot.common.util.ResponseUtils;
import com.youlai.boot.system.service.ConfigService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * IP限流过滤器
 *
 * @author Theo
 * @since 2024/08/10 14:38
 */
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConfigService configService;

    public RateLimiterFilter(RedisTemplate<String, Object> redisTemplate, ConfigService configService) {
        this.redisTemplate = redisTemplate;
        this.configService = configService;
    }

    /**
     * 确认是否限流方法
     * 默认情况下：限制同一个IP的QPS最大为10,可以通过修改系统配置进行调整
     * 这里也可以进行扩展，比如redis记录同一个ip每天出发限流的上限次数，记录在redis中，达到某个阈值后，进行永久封禁这个ip
     *
     * @param ip ip地址
     * @return  是否限流
     */
    public boolean rateLimit(String ip) {
        String key = RedisConstants.IP_RATE_LIMITER_KEY + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null || count == 1) {
            redisTemplate.expire(key,1, TimeUnit.SECONDS);
        }
        Object systemConfig = configService.getSystemConfig(RedisConstants.IP_QPS_THRESHOLD_LIMIT_KEY);
        long limit = 10;
        if(systemConfig != null){
            limit =  Long.parseLong(systemConfig.toString());
        }else{
            log.warn("[RedisRateLimiterFilter.rateLimit]系统配置中未配置IP请求限制QPS阈值配置,使用默认值:{},请检查配置项:{}",
                    limit,RedisConstants.IP_QPS_THRESHOLD_LIMIT_KEY);
        }
        return count != null && count > limit;
    }

    /**
     * IP限流过滤器
     * 默认情况下：限制同一个IP在一分钟内只能访问10次，可以通过修改系统配置进行调整
     *
     * @param request 请求体
     * @param response 响应体
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String ip = IPUtils.getIpAddr(request);
        if (rateLimit(ip)) {
            ResponseUtils.writeErrMsg(response, ResultCode.FLOW_LIMITING);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
