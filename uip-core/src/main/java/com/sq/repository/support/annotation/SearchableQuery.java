package com.sq.repository.support.annotation;

import com.sq.repository.callback.SearchCallback;

import java.lang.annotation.*;

/**
 * 覆盖默认的根据条件查询数据.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 16:50
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchableQuery {

    /**
     * 覆盖默认的查询所有ql
     *
     * @return
     */
    String findAllQuery() default "";

    /**
     * 覆盖默认的统计所有ql
     *
     * @return
     */
    String countAllQuery() default "";

    /**
     * 给ql拼条件及赋值的回调类型
     *
     * @return com.sq.repository.callback.SearchCallback子类
     */
    Class<? extends SearchCallback> callbackClass() default SearchCallback.class;

}
