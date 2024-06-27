package com.youlai.system.plugin.norepeat.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.RegisteredPayload;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.exception.BusinessException;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.plugin.norepeat.annotation.PreventRepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 处理重复提交的切面
 *
 * @author haoxr
 * @since 2.3.0
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DuplicateSubmitAspect {

    private final RedissonClient redissonClient;
    private static final String RESUBMIT_LOCK_PREFIX = "LOCK:RESUBMIT:";

    /**
     * 防重复提交切点
     */
    @Pointcut("@annotation(preventRepeatSubmit)")
    public void preventDuplicateSubmitPointCut(PreventRepeatSubmit preventRepeatSubmit) {
        log.info("定义防重复提交切点");
    }

    @Around("preventDuplicateSubmitPointCut(preventRepeatSubmit)")
    public Object doAround(ProceedingJoinPoint pjp, PreventRepeatSubmit preventRepeatSubmit) throws Throwable {

        String resubmitLockKey = generateResubmitLockKey();
        if (resubmitLockKey != null) {
            int expire = preventRepeatSubmit.expire(); // 防重提交锁过期时间
            RLock lock = redissonClient.getLock(resubmitLockKey);
            boolean lockResult = lock.tryLock(0, expire, TimeUnit.SECONDS); // 获取锁失败，直接返回 false
            if (!lockResult) {
                throw new BusinessException(ResultCode.REPEAT_SUBMIT_ERROR); // 抛出重复提交提示信息
            }
        }
        return pjp.proceed();
    }


    /**
     * 获取重复提交锁的 key
     */
    private String generateResubmitLockKey() {
        String resubmitLockKey = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
            // 从 JWT Token 中获取 jti
            String jti = (String) JWTUtil.parseToken(token).getPayload(RegisteredPayload.JWT_ID);
            resubmitLockKey = RESUBMIT_LOCK_PREFIX + jti + ":" + request.getMethod() + "-" + request.getRequestURI();
        }
        return resubmitLockKey;
    }

}
