package com.sq.repository.support.annotation;

import java.lang.annotation.*;

/**
 * 开启查询缓存注解.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 9:56
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableQueryCache {
    boolean value() default true;
}
