package com.youlai.system.common.annotation;


import java.lang.annotation.*;

/**
 * 防止重复提交注解
 *
 * @author haoxr
 * @since 2.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreventDuplicateSubmit {

    /**
     * 防重提交锁过期时间(秒)
     * <p>
     * 默认5秒内不允许重复提交
     */
    int expire() default 5;

}
