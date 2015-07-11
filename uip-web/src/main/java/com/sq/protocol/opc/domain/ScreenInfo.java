package com.sq.protocol.opc.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 大屏显示信息.
 * User: shuiqing
 * Date: 2015/7/6
 * Time: 19:18
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "t_njmb_xsp")
public class ScreenInfo extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false, precision=10)
    private Long id;

    private String xiangmu;

    private String bianma;

    private String guobiao;

    private String guobiao2;

    private String oumeng;

    private String guolu1;

    private String guolu2;

    private String guolu3;

    private String guolu4;

    private int serialno;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getXiangmu() {
        return xiangmu;
    }

    public void setXiangmu(String xiangmu) {
        this.xiangmu = xiangmu;
    }

    public String getBianma() {
        return bianma;
    }

    public void setBianma(String bianma) {
        this.bianma = bianma;
    }

    public String getGuobiao() {
        return guobiao;
    }

    public void setGuobiao(String guobiao) {
        this.guobiao = guobiao;
    }

    public String getOumeng() {
        return oumeng;
    }

    public void setOumeng(String oumeng) {
        this.oumeng = oumeng;
    }

    public String getGuolu1() {
        return guolu1;
    }

    public void setGuolu1(String guolu1) {
        this.guolu1 = guolu1;
    }

    public String getGuolu2() {
        return guolu2;
    }

    public void setGuolu2(String guolu2) {
        this.guolu2 = guolu2;
    }

    public String getGuolu3() {
        return guolu3;
    }

    public void setGuolu3(String guolu3) {
        this.guolu3 = guolu3;
    }

    public String getGuolu4() {
        return guolu4;
    }

    public void setGuolu4(String guolu4) {
        this.guolu4 = guolu4;
    }

    public int getSerialno() {
        return serialno;
    }

    public void setSerialno(int serialno) {
        this.serialno = serialno;
    }

    public String getGuobiao2() {
        return guobiao2;
    }

    public void setGuobiao2(String guobiao2) {
        this.guobiao2 = guobiao2;
    }
}
