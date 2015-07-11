package com.mongo.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/7/1
 * Time: 14:13
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Document(collection = "OriginalDataRealTime")
public class MongoOrignalDataRealTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    /** 指标的CODE */
    private String itemCode;

    /** 指标的同步值 */
    private Double itemValue;

    /** 获取指标实例的时间点 */
    private String instanceTime;

    /** 数据获取批次号 */
    private Long batchNum;

    /** 系统编号 */
    private int sysId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public String getInstanceTime() {
        return instanceTime;
    }

    public void setInstanceTime(String instanceTime) {
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
}
