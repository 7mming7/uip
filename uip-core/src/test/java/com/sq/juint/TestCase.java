package com.sq.juint;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Junit 测试基类，主要是为了加载配置文件
 * 使得继承此类的子类不需要再去加载配置文件
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:46
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-test.xml"})
public class TestCase{

    @PersistenceContext
    protected EntityManager entityManager;

    public void clear() {
        flush();
        entityManager.clear();
    }

    public void flush() {
        entityManager.flush();
    }


    protected String nextRandom() {
        return System.currentTimeMillis() + RandomStringUtils.randomNumeric(5);
    }
}

