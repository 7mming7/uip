package com.sq.loadometer.domain;

import com.sq.annotation.NativeQueryResultColumn;
import com.sq.annotation.NativeQueryResultEntity;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.SqlResultSetMapping;
import java.io.Serializable;

/**
 * 地磅指标DTO
 * User: shuiqing
 * Date: 2015/9/15
 * Time: 16:04
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@NativeQueryResultEntity
public class LoadometerIndicatorDto implements Serializable {

    private static final long serialVersionUID = 4275590174385725787L;

    @NativeQueryResultColumn(index=0)
    private String sourceCode;

    @NativeQueryResultColumn(index=1)
    private String indicatorCode;

    @NativeQueryResultColumn(index=2)
    private String totalAmount;

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
