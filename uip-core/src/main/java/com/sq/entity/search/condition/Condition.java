package com.sq.entity.search.condition;

import com.sq.entity.search.MatchType;

/**
 * 简单查询条件对象.
 * User: shuiqing
 * Date: 2015/2/21
 * Time: 15:11
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class Condition implements PropertyFilter {

    // ==========================================
    // fields...

    /** 查询参数分隔符. */
    public static final String separator = "_";

    /** 模型属性名. */
    private String property;

    /** 条件判断的符号，比如 >=，=，is， is not 等等.*/
    private MatchType matchType;

    /** 条件设置的值.*/
    private Object matchValue;

    // ==========================================
    // constructor...

    private Condition(String property, MatchType matchType, Object matchValue) {
        this.property = property;
        this.matchType = matchType;
        this.matchValue = matchValue;
    }

    // ==========================================
    // 工具方法...
    /**
     * 根据查询属性、操作符和值生成Condition
     * @param property   查询属性
     * @param matchType  匹配操作符
     * @param matchValue 匹配参数
     * @return 构建出的查询条件
     */
    public static Condition newCondition(final String property, final MatchType matchType, final Object matchValue) {
        return new Condition(property, matchType, matchValue);
    }

    /**
     * 获取当前condition的操作符
     * @return 操作符
     */
    public String getOperatorStr () {
        if (matchType != null) {
            return matchType.getSymbol();
        }
        return "";
    }

    // ==========================================
    // getter and setter method...

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Object getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(Object matchValue) {
        this.matchValue = matchValue;
    }

    // ==========================================
    // override method...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition that = (Condition) o;

        if (property != null ? !property.equals(that.property) : that.property != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return property != null ? property.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "property='" + property + '\'' +
                ", matchType=" + matchType.getInfo() +
                ", matchValue=" + matchValue +
                '}';
    }
}
