package com.youlai.system.plugin.syslog.annotation;

import com.youlai.system.enums.LogModuleEnum;

import java.lang.annotation.*;

/**
 * 日志注解
 *
 * @author Ray
 * @since 2024/6/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogAnnotation {

    String value() default "";

    LogModuleEnum module()  ;


}