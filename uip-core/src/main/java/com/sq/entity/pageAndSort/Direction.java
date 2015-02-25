package com.sq.entity.pageAndSort;

import java.util.Locale;

/**
 * 排序方向.
 * User: shuiqing
 * Date: 2015/2/25
 * Time: 20:23
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public enum Direction {

    /** 正序*/
    ASC,

    /** 逆序*/
    DESC;

    /**
     * Returns the {@link Direction} enum for the given {@link String} value.
     *
     * @param value
     * @throws IllegalArgumentException in case the given value cannot be parsed into an enum value.
     * @return
     */
    public static Direction fromString(String value) {

        try {
            return Direction.valueOf(value.toUpperCase(Locale.US));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(
                    "Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
        }
    }

    /**
     * Returns the {@link Direction} enum for the given {@link String} or null if it cannot be parsed into an enum
     * value.
     *
     * @param value
     * @return
     */
    public static Direction fromStringOrNull(String value) {

        try {
            return fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
