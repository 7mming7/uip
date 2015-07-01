package com.mongo.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

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
@Document(collection = "OriginalDataHistory")
public class MongoOriginalDataHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    /** 指标的CODE */
    private String itemCode;

    /** 指标的同步值 */
    private String itemValue;

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

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
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
