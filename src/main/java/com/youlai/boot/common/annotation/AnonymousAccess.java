package com.youlai.boot.common.annotation;

import java.lang.annotation.*;

/// 标记匿名访问
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousAccess {
}
