package com.sq.comput.domain;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 指标实例.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 9:53
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name="t_IndicatorInstanceCurrent")
public class IndicatorInstance extends IndicatorBase {

    private static final long serialVersionUID = -6188768929459925207L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的指标项
     */
    private Long indicatorTempId;

    /**
     * 指标值类型
     */
    private int valueType;

    private String stringValue;

    private double floatValue;

    /**
     * 指标获取时间
     */
    private Calendar instanceTime;

    /**
     * 所属指标分类
     */
    private Long categoryId;

    /**
     * 统计日期
     */
    private int statDateNum;

    @OneToOne
    @JoinColumn(name="parentIndiInsId")
    @ForeignKey(name="fk_ii_parentIndiInsId")
    private IndicatorInstance parentIndiIns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndicatorTempId() {
        return indicatorTempId;
    }

    public void setIndicatorTempId(Long indicatorTempId) {
        this.indicatorTempId = indicatorTempId;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public double getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(double floatValue) {
        this.floatValue = floatValue;
    }

    public Calendar getInstanceTime() {
        return instanceTime;
    }

    public void setInstanceTime(Calendar instanceTime) {
        this.instanceTime = instanceTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatDateNum() {
        return statDateNum;
    }

    public void setStatDateNum(int statDateNum) {
        this.statDateNum = statDateNum;
    }

    public IndicatorInstance getParentIndiIns() {
        return parentIndiIns;
    }

    public void setParentIndiIns(IndicatorInstance parentIndiIns) {
        this.parentIndiIns = parentIndiIns;
    }
}