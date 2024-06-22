package com.youlai.system.plugin.norepeat.annotation;


import java.lang.annotation.*;

/**
 * 防止重复提交注解
 * <p>
 * 该注解用于方法上，防止在指定时间内的重复提交。
 * 默认时间为5秒。
 *
 * @author haoxr
 * @since 2.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreventRepeatSubmit {

    /**
     * 锁过期时间（秒）
     * <p>
     * 默认5秒内不允许重复提交
     */
    int expire() default 5;

}
