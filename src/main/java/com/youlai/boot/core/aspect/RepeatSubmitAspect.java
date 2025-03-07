package com.youlai.boot.core.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.RegisteredPayload;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.config.property.SecurityProperties;
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
 * 防重复提交切面
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RepeatSubmitAspect {

    private final RedissonClient redissonClient;
    private final SecurityProperties securityProperties;

    /**
     * 防重复提交切点
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void repeatSubmitPointCut(RepeatSubmit repeatSubmit) {
        log.debug("定义防重复提交切点，注解：{}", repeatSubmit);
    }


    /**
     * 环绕通知：处理防重复提交逻辑
     */
    @Around("repeatSubmitPointCut(repeatSubmit)")
    public Object handleRepeatSubmit(ProceedingJoinPoint pjp, RepeatSubmit repeatSubmit) throws Throwable {
        String lockKey = buildLockKey();
        if (lockKey == null) {
            log.warn("无法生成防重复提交锁的 key，跳过防重复提交逻辑");
            return pjp.proceed();
        }

        int expire = repeatSubmit.expire(); // 防重提交锁过期时间
        RLock lock = redissonClient.getLock(lockKey);

        boolean locked = lock.tryLock(0, expire, TimeUnit.SECONDS);
        log.info("获取防重复提交锁，key：{}，是否成功：{}", lockKey, locked);
        if (!locked) {
            log.warn("重复提交请求，锁 key：{}", lockKey);
            throw new BusinessException(ResultCode.USER_DUPLICATE_REQUEST);
        }

        return pjp.proceed();
    }


    private String buildLockKey() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 统一校验 Token 格式
        if (StrUtil.isBlank(tokenHeader) || !tokenHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX )) {
            log.warn("请求头中未找到有效的 Token");
            return null;
        }

        String rawToken = tokenHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX .length());

        String tokenHash = DigestUtil.sha256Hex(rawToken); // 建议替换为 SHA256 更安全

        return RedisConstants.RESUBMIT_LOCK_PREFIX
                + tokenHash + ":"
                + request.getMethod() + "-"
                + request.getRequestURI();
    }
}
