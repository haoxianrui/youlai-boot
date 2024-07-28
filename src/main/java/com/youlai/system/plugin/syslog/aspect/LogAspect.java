package com.youlai.system.plugin.syslog.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.util.IPUtils;
import com.youlai.system.model.entity.SysLog;
import com.youlai.system.plugin.syslog.annotation.LogAnnotation;
import com.youlai.system.security.util.SecurityUtils;
import com.youlai.system.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LogAspect {

    private final SysLogService logService;
    private final HttpServletRequest request;

    @Pointcut("@annotation(com.youlai.system.plugin.syslog.annotation.LogAnnotation)")
    public void logPointcut() {
    }

    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogAnnotation logAnnotation) throws Throwable {
        String requestURI = request.getRequestURI();

        Long userId = null;
        // 非登录请求获取用户ID，登录请求在登录成功后(joinPoint.proceed())获取用户ID
        if (!SecurityConstants.LOGIN_PATH.equals(requestURI)) {
            userId = SecurityUtils.getUserId();
        }

        TimeInterval timer = DateUtil.timer();
        // 执行方法
        Object proceed = joinPoint.proceed();
        long executionTime = timer.interval();

        // 创建日志记录
        SysLog log = new SysLog();
        log.setModule(logAnnotation.module());
        log.setContent(logAnnotation.value());
        log.setRequestUri(requestURI);
        // 登录方法需要在登录成功后获取用户ID
        if (userId == null) {
            userId = SecurityUtils.getUserId();
        }
        log.setCreateBy(userId);
        String ipAddr = IPUtils.getIpAddr(request);
        if (StrUtil.isNotBlank(ipAddr)) {
            log.setIp(ipAddr);
            String region = IPUtils.getRegion(ipAddr);
            // 中国|0|四川省|成都市|电信 解析省和市
            if (StrUtil.isNotBlank(region)) {
                String[] regionArray = region.split("\\|");
                if (regionArray.length > 2) {
                    log.setProvince(regionArray[2]);
                    log.setCity(regionArray[3]);
                }
            }
        }
        log.setExecutionTime(executionTime);
        // 获取浏览器和终端系统信息
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        // 系统信息
        log.setOs(userAgent.getOs().getName());
        // 浏览器信息
        log.setBrowser(userAgent.getBrowser().getName());
        log.setBrowserVersion(userAgent.getBrowser().getVersion(userAgentString));
        // 保存日志到数据库
        logService.save(log);

        return proceed;
    }


}
