package com.sq.juint;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:51
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public abstract class BaseUserIT extends TestCase {

    public User createUser() {
        User user = new User();
        user.setUsername("zhangkaitao$$$" + System.nanoTime() + RandomStringUtils.random(10));
        user.setPassword("123456");
        user.setRegisterDate(new Date());

        return user;
    }
}
