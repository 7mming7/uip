package com.sq.entity.search.exception;

import com.sq.entity.search.MatchType;
import com.sq.exception.SearchException;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/17
 * Time: 14:08
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public final class InvlidSearchOperatorException extends SearchException {

    public InvlidSearchOperatorException(String searchProperty, String operatorStr) {
        this(searchProperty, operatorStr, null);
    }

    public InvlidSearchOperatorException(String searchProperty, String operatorStr, Throwable cause) {
        super("Invalid Search Operator searchProperty [" + searchProperty + "], " +
                "operator [" + operatorStr + "], must be one of " + MatchType.toStringAllOperator(), cause);
    }
}
