package com.sq.entity.search.condition;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.exception.InvlidSearchOperatorException;
import com.sq.exception.SearchException;
import com.sq.util.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

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
public class Condition implements SearchFilter {

    // ==========================================
    // fields...

    /** 查询参数分隔符. */
    public static final String separator = "_";

    private String key;

    /** 模型属性名. */
    private String property;

    /** 条件判断的符号，比如 >=，=，is， is not 等等.*/
    private MatchType matchType;

    /** 条件设置的值.*/
    private Object matchValue;

    // ==========================================
    // constructor...

    Condition(String property, MatchType matchType, Object matchValue) {
        this.property = property;
        this.matchType = matchType;
        this.matchValue = matchValue;
        this.key = this.property + separator + this.matchType;
    }

    // ==========================================
    // 工具方法...

    /**
     * 根据查询key和值生成Condition
     * @param key
     * @param value
     * @return Condition
     * @throws SearchException 查询exception
     */
    static Condition newCondition(final String key, final Object value) throws SearchException {

        Assert.notNull(key, "Condition key must not null");

        String[] searchs = StringUtils.split(key, separator);

        if (searchs.length == 0) {
            throw new SearchException("Condition key format must be : property or property_op");
        }

        String searchProperty = searchs[0];

        MatchType matchType = null;
        if (searchs.length == 1) {
            matchType = MatchType.CUSTOM;
        } else {
            try {
                matchType = MatchType.valueOf(searchs[1]);
            } catch (IllegalArgumentException e) {
                throw new InvlidSearchOperatorException(searchProperty, searchs[1]);
            }
        }

        boolean allowBlankValue = MatchType.isAllowBlankValue(matchType);
        boolean isValueBlank = value == null;
        isValueBlank = isValueBlank || (value instanceof String && StringUtils.isBlank((String) value));
        isValueBlank = isValueBlank || (value instanceof List && ((List) value).size() == 0);
        //过滤掉空值，即不参与查询
        if (!allowBlankValue && isValueBlank) {
            return null;
        }

        Condition searchFilter = newCondition(searchProperty, matchType, value);

        return searchFilter;
    }

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
     * 获取 操作符
     *
     * @return
     */
    public MatchType getOperator() throws InvlidSearchOperatorException {
        return matchType;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // ==========================================
    // override method...

    /**
     * 得到实体属性名
     *
     * @return
     */
    public String getEntityProperty() {
        return property;
    }

    /**
     * 是否是一元过滤 如is null is not null
     *
     * @return
     */
    public boolean isUnaryFilter() {
        String operatorStr = getOperator().getSymbol();
        return operatorStr.startsWith("is");
    }

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
