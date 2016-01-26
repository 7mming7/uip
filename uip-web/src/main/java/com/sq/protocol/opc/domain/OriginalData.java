package com.sq.protocol.opc.domain;

import com.sq.entity.AbstractEntity;
import com.sq.entity.BaseEntity;
import com.sq.util.DateUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 测点同步结果集.
 * User: shuiqing
 * Date: 2015/4/6
 * Time: 19:07
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "t_OriginalData")
public class OriginalData extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false, precision=10)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /** 指标的CODE */
    private String itemCode;

    /** 指标的同步值 */
    private String itemValue;

    /** 获取指标实例的时间点 */
    private Calendar instanceTime;

    /** 数据获取批次号 */
    private Long batchNum;

    /** 系统编号 */
    private int sysId;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public Calendar getInstanceTime() {
        return instanceTime;
    }

    public void setInstanceTime(Calendar instanceTime) {
        this.instanceTime = instanceTime;
    }

    public Long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Long batchNum) {
        this.batchNum = batchNum;
    }

    public int getSysId() {
        return sysId;
    }

    public void setSysId(int sysId) {
        this.sysId = sysId;
    }

    @Override
    public String toString() {
        return "OriginalData{" +
                "sysId=" + sysId +
                ", itemCode='" + itemCode + '\'' +
                ", itemValue='" + itemValue + '\'' +
                ", instanceTime=" + DateUtil.formatCalendar(instanceTime,DateUtil.DATE_FORMAT_DAFAULTYMDHMS) +
                ", batchNum=" + batchNum +
                '}';
    }
}
