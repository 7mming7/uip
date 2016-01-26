package com.sq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: shuiqing
 * Date: 16/1/18
 * Time: 上午10:18
 * Email: annuus.sq@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class NumberUtils {

    /**
     * 使用正则表达式判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static void main (String[] args) {
        System.out.println(NumberUtils.isNumeric("4.7400    ".trim()));
    }
}
