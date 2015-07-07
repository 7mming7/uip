package com.sq.comput.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.MappedSuperclass;
import java.util.Calendar;

/**
 * 指标限值基类.
 * User: shuiqing
 * Date: 2015/5/14
 * Time: 16:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@MappedSuperclass
public abstract class LimitBase extends AbstractEntity<Long> implements Cloneable  {

    /** 类型描述字典值 -> IndicatorConsts Limit attribute */
    private int attribute;

    private String description;

    /** 限值类型-> IndicatorConsts */
    private int limitType;

    /** 值类型，定值还是表达式-> IndicatorConsts */
    private int expType;

    private String expression;

    private Calendar createTime;

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getExpType() {
        return expType;
    }

    public void setExpType(int expType) {
        this.expType = expType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }
}