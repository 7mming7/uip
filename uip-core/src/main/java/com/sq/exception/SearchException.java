package com.sq.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/2/20
 * Time: 20:41
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class SearchException extends NestedRuntimeException {

    public SearchException(String msg) {
        super(msg);
    }

    public SearchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}