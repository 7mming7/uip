package com.sq.quota.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.MappedSuperclass;

/**
 * 指标基类.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io
 * _
 * |_)._ _
 * | o| (_
 */
@MappedSuperclass
public abstract class QuotaBase extends AbstractEntity<Long> implements Cloneable {

    /** 指标编码 */
    private String indicatorCode;

    /** 指标名称 */
    private String indicatorName;

    /**
     * 数据来源方式
     * 字典值  tableName->INDICATORBASE  attribute->DATASOURCE
     * 1---录入
     * 2---计算
     * 3---接口
     */
    private int dataSource;

    /**
     * 指标数值单位
     * 字典值 tableName->INDICATORBASE  attribute->UNIT
     */
    private int unit;

    /** 小数点位数 */
    private int decimalNum;

    /** 计算维度    */
    private int fetchCycle;

    /** 计算频率 */
    private Integer calFrequency;

    /**
     * 数据聚合方式
     *   最大值、最小值、算术平均值、加权平均值、积分值、累计值、差值
     */
    private Integer operCalType;

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public int getDataSource() {
        return dataSource;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getDecimalNum() {
        return decimalNum;
    }

    public void setDecimalNum(int decimalNum) {
        this.decimalNum = decimalNum;
    }

    public int getFetchCycle() {
        return fetchCycle;
    }

    public void setFetchCycle(int fetchCycle) {
        this.fetchCycle = fetchCycle;
    }

    public Integer getOperCalType() {
        return operCalType;
    }

    public void setOperCalType(Integer operCalType) {
        this.operCalType = operCalType;
    }

    public Integer getCalFrequency() {
        return calFrequency;
    }

    public void setCalFrequency(Integer calFrequency) {
        this.calFrequency = calFrequency;
    }
}