package com.sq.exception;

/**
 * 调用了尚未提供支持的方法时抛出.
 * User: shuiqing
 * Date: 2015/3/30
 * Time: 15:02
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class UnsupportedValueException extends RuntimeException {

    private static final long serialVersionUID = -8817834237727533811L;

    public UnsupportedValueException() {
        super();
    }

    public UnsupportedValueException(String message) {
        super(message);
    }

    public UnsupportedValueException(Throwable cause) {
        super(cause);
    }

    public UnsupportedValueException(String message, Throwable cause) {
        super(message, cause);
    }

}
