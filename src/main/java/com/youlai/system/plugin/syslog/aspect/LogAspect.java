package com.youlai.system.plugin.syslog.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.extra.servlet.ServletUtil;
import com.youlai.system.common.util.IPUtils;
import com.youlai.system.model.entity.SysLog;
import com.youlai.system.plugin.syslog.annotation.LogAnnotation;
import com.youlai.system.security.util.SecurityUtils;
import com.youlai.system.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 *
 * @author Ray
 * @since 2024/6/25
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogService logService;
    private final HttpServletRequest request;

    @Pointcut("@annotation(com.youlai.system.plugin.syslog.annotation.LogAnnotation)")
    public void logPointcut() {
    }

    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogAnnotation logAnnotation) throws Throwable {
        TimeInterval timer = DateUtil.timer();
        Object proceed = joinPoint.proceed();
        long executionTime =timer.interval();

        // 创建日志对象
        SysLog log = new SysLog();
        log.setType(logAnnotation.logType().getValue());
        log.setTitle(logAnnotation.value());
        log.setRequestUri(request.getRequestURI());
        log.setIp(IPUtils.getIpAddr(request));
        log.setExecutionTime(executionTime);
        log.setCreateBy(SecurityUtils.getUserId());

        // 保存日志到数据库
        logService.save(log);

        return proceed;
    }






}
