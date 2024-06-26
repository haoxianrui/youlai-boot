package com.youlai.system.plugin.syslog.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.OS;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
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
        long executionTime = timer.interval();

        // 创建日志对象
        SysLog log = new SysLog();
        log.setType(logAnnotation.logType().getValue());
        log.setTitle(logAnnotation.value());
        log.setRequestUri(request.getRequestURI());
        log.setIp(IPUtils.getIpAddr(request));
        log.setExecutionTime(executionTime);
        Long userId = SecurityUtils.getUserId();
        log.setCreateBy(userId);
        // 方法名
        log.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // 获取浏览器和终端系统信息
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);

        // 设置系统信息
        log.setOs( userAgent.getOs().getName());

        String browserName = userAgent.getBrowser().getName();
        String browserVersion = userAgent.getBrowser().getVersion(userAgentString);

        // 设置浏览器信息
        String browserInfo = browserVersion != null && !browserVersion.isEmpty() ? browserName + " " + browserVersion : browserName;
        log.setBrowser(browserInfo);
        // 保存日志到数据库
        logService.save(log);




        return proceed;
    }


}
