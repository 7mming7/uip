package com.sq.loadometer.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * 垃圾称重(标准)
 * User: shuiqing
 * Date: 15/11/24
 * Time: 上午10:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name="T_Trade")
public class Trade extends AbstractEntity<Long> implements Serializable {

    public Trade() {
    }

    public Trade(Map<String,Object> map) {
        this.lsh = map.get("lsh") != null ? map.get("lsh").toString() : null;
        this.carNo = map.get("carNo") != null ? map.get("carNo").toString() : null;
        this.proCode = map.get("proCode") != null ? map.get("proCode").toString() : null;
        this.sourceArea = map.get("sourceArea") != null ? map.get("sourceArea").toString() : null;
        this.firstWeightTime = map.get("firstWeightTime") != null ? map.get("firstWeightTime").toString() : null;
        this.secondWeightTime = map.get("secondWeightTime") != null ? map.get("secondWeightTime").toString() : null;
        this.gross = map.get("gross") != null ? map.get("gross").toString() : null;
        this.tare = map.get("tare") != null ? map.get("tare").toString() : null;
        this.net = map.get("net") != null ? map.get("net").toString() : null;
        this.operator = map.get("operator") != null ? map.get("operator").toString() : null;
        this.statDateNum = map.get("statDateNum") != null ? (Integer)map.get("statDateNum") : null;
    }

    /**
     * 唯一标示
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流水号 */
    private String lsh;

    /** 车号 */
    private String carNo;

    /** 货物编号 */
    private String proCode;

    /** 货物来源区域 */
    private String sourceArea;

    /** 入厂时间 */
    private String firstWeightTime;

    /** 出厂时间 */
    private String secondWeightTime;

    /** 毛重 */
    private String gross;

    /** 皮重 */
    private String tare;

    /** 净重 */
    private String net;

    /** 操作员 */
    private String operator;

    /** 统计日期  格式如  YYYYMMDD */
    private Integer statDateNum;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getProCode() {
        return proCode;
    }

    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    public String getSourceArea() {
        return sourceArea;
    }

    public void setSourceArea(String sourceArea) {
        this.sourceArea = sourceArea;
    }

    public String getFirstWeightTime() {
        return firstWeightTime;
    }

    public void setFirstWeightTime(String firstWeightTime) {
        this.firstWeightTime = firstWeightTime;
    }

    public String getSecondWeightTime() {
        return secondWeightTime;
    }

    public void setSecondWeightTime(String secondWeightTime) {
        this.secondWeightTime = secondWeightTime;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getTare() {
        return tare;
    }

    public void setTare(String tare) {
        this.tare = tare;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getStatDateNum() {
        return statDateNum;
    }

    public void setStatDateNum(Integer statDateNum) {
        this.statDateNum = statDateNum;
    }
}
