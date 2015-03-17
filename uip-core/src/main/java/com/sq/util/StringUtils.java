package com.sq.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

/**
 * 字符串处理相关工具类.
 * User: shuiqing
 * Date: 2015/2/20
 * Time: 20:46
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class StringUtils {
    protected StringUtils() {
    }

    public static boolean isEmpty(String text) {
        return org.apache.commons.lang3.StringUtils.isEmpty(text);
    }

    public static boolean isBlank(String text) {
        return org.apache.commons.lang3.StringUtils.isBlank(text);
    }

    public static boolean isNotBlank(String text) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(text);
    }

    public static String capitalize(String text) {
        return org.apache.commons.lang3.StringUtils.capitalize(text);
    }

    public static String substring(String text, int offset, int limit) {
        return org.apache.commons.lang3.StringUtils.substring(text, offset,
                limit);
    }

    public static String substringBefore(String text, String token) {
        return org.apache.commons.lang3.StringUtils
                .substringBefore(text, token);
    }

    public static String substringAfter(String text, String token) {
        return org.apache.commons.lang3.StringUtils.substringAfter(text, token);
    }

    public static String[] splitByWholeSeparator(String text, String separator) {
        return org.apache.commons.lang3.StringUtils.splitByWholeSeparator(text,
                separator);
    }

    public static String join(List list, String separator) {
        return org.apache.commons.lang3.StringUtils.join(list, separator);
    }

    public static String[] split(String str, String separatorChar) {
        return org.apache.commons.lang3.StringUtils.split(str, separatorChar);
    }

    public static String escapeHtml(String text) {
        return StringEscapeUtils.escapeHtml4(text);
    }

    public static String unescapeHtml(String text) {
        return StringEscapeUtils.unescapeHtml4(text);
    }

    public static String escapeXml(String text) {
        return StringEscapeUtils.escapeHtml4(text);
    }

    public static String unescapeXml(String text) {
        return StringEscapeUtils.unescapeXml(text);
    }

    public static String trim(String text) {
        if (text == null) {
            return null;
        }

        text = text.replace("" + ((char) 160), " ");

        text = org.apache.commons.lang3.StringUtils.trim(text);
        text = org.apache.commons.lang3.StringUtils.strip(text, "　");

        return text;
    }
}