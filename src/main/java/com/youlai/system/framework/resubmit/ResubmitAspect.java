package com.youlai.system.framework.resubmit;

import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.exception.BusinessException;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.RequestUtils;
import com.youlai.system.framework.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 *
 * @author : haoxr
 * @since : 2023/05/09
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ResubmitAspect {

    private final RedissonClient redissonClient;

    private final JwtTokenManager jwtTokenManager;

    private static final String RESUBMIT_LOCK_PREFIX = "LOCK:RESUBMIT:";

    /**
     * 防重复提交切点
     */
    @Pointcut("@annotation(resubmit)")
    public void preventDuplicateSubmitPointCut(Resubmit resubmit) {
        log.info("定义防重复提交切点");
    }

    @Around("preventDuplicateSubmitPointCut(resubmit)")
    public Object doAround(ProceedingJoinPoint pjp, Resubmit resubmit) throws Throwable {

        String resubmitLockKey = generateResubmitLockKey();
        if (resubmitLockKey != null) {
            int expire = resubmit.expire(); // 防重提交锁过期时间
            RLock lock = redissonClient.getLock(resubmitLockKey);
            boolean lockResult = lock.tryLock(0, expire, TimeUnit.SECONDS); // 获取锁失败，直接返回 false
            if (!lockResult) {
                throw new BusinessException(ResultCode.REPEAT_SUBMIT_ERROR); // 抛出重复提交提示信息
            }
        }
        Object result = pjp.proceed();
        return result;
    }


    /**
     * 获取防重提交锁的 key
     */
    private String generateResubmitLockKey() {
        String resubmitLockKey = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String jwt = RequestUtils.resolveToken(request);
        if (StrUtil.isNotBlank(jwt)) {
            String jti = (String) jwtTokenManager.getTokenClaims(jwt).get("jti");
            resubmitLockKey = RESUBMIT_LOCK_PREFIX + jti + ":" + request.getMethod() + "-" + request.getRequestURI() + "-";
        }
        return resubmitLockKey;
    }

}
