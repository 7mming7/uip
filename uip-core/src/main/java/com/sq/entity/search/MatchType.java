package com.sq.entity.search;

import com.sq.exception.SearchException;
import com.sq.util.StringUtils;

import java.util.Arrays;

/**
 * 匹配操作符，SQL中的逻辑运算的连接符.
 * User: shuiqing
 * Date: 2015/2/20
 * Time: 16:59
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public enum MatchType {

    EQ("等于", "="),

    NE("不等于", "!="),

    GT("大于", ">"),

    GTE("大于等于", ">="),

    LT("小于", "<"),

    LTE("小于等于", "<="),

    prefixLike("前缀模糊匹配", "like"),

    prefixNotLike("前缀模糊不匹配", "not like"),

    suffixLike("后缀模糊匹配", "like"),

    suffixNotLike("后缀模糊不匹配", "not like"),

    LIKE("模糊匹配", "like"),

    notLike("不匹配", "not like"),

    isNull("空", "is null"),

    isNotNull("非空", "is not null"),

    IN("包含", "in"),

    notIn("不包含", "not in"),

    CUSTOM("自定义默认的", null);

    private final String info;

    private final String symbol;

    MatchType(final String info, String symbol) {
        this.info = info;
        this.symbol = symbol;
    }

    public String getInfo() {
        return info;
    }

    public String getSymbol() {
        return symbol;
    }

    public static String toStringAllOperator() {
        return Arrays.toString(MatchType.values());
    }

    /**
     * 操作符是否允许为空
     *
     * @param operator
     * @return
     */
    public static boolean isAllowBlankValue(final MatchType operator) {
        return operator == MatchType.isNotNull || operator == MatchType.isNull;
    }


    public static MatchType valueBySymbol(String symbol) throws SearchException {
        symbol = formatSymbol(symbol);
        for (MatchType operator : values()) {
            if (operator.getSymbol().equals(symbol)) {
                return operator;
            }
        }

        throw new SearchException("MatchType not method search operator symbol : " + symbol);
    }

    private static String formatSymbol(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return symbol;
        }
        return symbol.trim().toLowerCase().replace("  ", " ");
    }
}
