package com.youlai.boot.common.annotation;

import com.youlai.boot.common.enums.LogModuleEnum;

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
public @interface Log {

    /**
     * 日志描述
     *
     * @return 日志描述
     */
    String value() default "";

    /**
     * 日志模块
     *
     * @return 日志模块
     */

    LogModuleEnum module();

    /**
     * 是否记录请求参数
     *
     * @return 是否记录请求参数
     */
    boolean params() default true;

    /**
     * 是否记录响应结果
     * <br/>
     * 响应结果默认不记录，避免日志过大
     * @return 是否记录响应结果
     */
    boolean result() default false;


}