package com.sq.inject.annotation;

import java.lang.annotation.*;

/**
 * 查找注解的字段作为BaseService/BaseRepository数据
 * 即
 * 1、查找对象中的注解了@BaseComponent的对象
 * 2、
 * 调用BaseCRUDController.setBaseService 设置BaseService
 * 调用BaseService.setBaseRepository 设置BaseRepository
 *
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 10:23
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseComponent {

}