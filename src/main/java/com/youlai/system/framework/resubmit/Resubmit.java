package com.youlai.system.framework.resubmit;


import java.lang.annotation.*;

/**
 * 防重复提交注解
 *
 * @author haoxr
 * @since 2023/5/9
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Resubmit {

    /**
     * 防重提交锁过期时间(秒)
     * <p>
     * 默认5秒内不允许重复提交
     */
    int expire() default 5;

}
