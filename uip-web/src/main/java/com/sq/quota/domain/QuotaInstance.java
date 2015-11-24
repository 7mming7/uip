package com.sq.quota.domain;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

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
public class QuotaInstance extends QuotaBase {

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

    private Double floatValue;

    /**
     * 指标获取时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date instanceTime;

    /** 创建时间 */
    private Calendar createTime;

    /**
     * 所属指标分类
     */
    private Long categoryId;

    /**
     * 统计日期
     */
    private int statDateNum;

    public QuotaInstance() {}

    public QuotaInstance(QuotaTemp indicatorTemplate) {
        this.categoryId = indicatorTemplate.getQuotaCategory() != null ? indicatorTemplate
                .getQuotaCategory().getId() : null;
        this.setDataSource(indicatorTemplate.getDataSource());
        this.setDecimalNum(indicatorTemplate.getDecimalNum());
        this.setFetchCycle(indicatorTemplate.getFetchCycle());
        this.setCalFrequency(indicatorTemplate.getCalFrequency());
        this.setIndicatorCode(indicatorTemplate.getIndicatorCode());
        this.setIndicatorName(indicatorTemplate.getIndicatorName());
        this.indicatorTempId = indicatorTemplate.getId();
        this.setUnit(indicatorTemplate.getUnit());
        this.setOperCalType(indicatorTemplate.getOperCalType());
    }

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

    public Date getInstanceTime() {
        return instanceTime;
    }

    public void setInstanceTime(Date instanceTime) {
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

    public Double getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Double floatValue) {
        this.floatValue = floatValue;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }
}