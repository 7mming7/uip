package com.sq.protocol.odbc.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 地磅数据进出场流水记录.
 * User: shuiqing
 * Date: 2015/5/12
 * Time: 11:57
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

    /**
     *
     */
    private static final long serialVersionUID = 2211368658057245960L;

    /**
     * 唯一标示
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //临时磅单号
    private String ticketno1;

    //交易磅单号
    private String ticketno2;

    //临时磅站
    private String station1;

    //交易磅站
    private String station2;

    //临时秤号
    private String scaleno1;

    //交易秤号
    private String scaleno2;

    //车号
    private String truckno;

    //卡号
    private String cardno;

    //合同号
    private String contractno;

    //货物名称
    private String productcode;

    //货物编号
    private String product;

    //规格号
    private String specification;

    //发货单位
    private String sender;

    //收货单位
    private String receiver;

    //运输单位
    private String transporter;

    //临时称重时间
    private Calendar firstdatetime;

    //交易称重时间
    private Calendar seconddatetime;

    //毛重称重时间
    private Calendar grossdatetime;

    //皮重称重时间
    private Calendar taredatetime;

    //临时重量
    private Integer firstweight;

    //交易重量
    private Integer secondweight;

    //毛重
    private Integer gross;

    //皮重
    private Integer tare;

    //净重
    private Integer net;

    //货物净重
    private Integer productnet;

    //扣水
    private Integer exceptwater;

    //扣杂
    private Integer exceptother;

    //临时称重操作员Id
    private String userid1;

    //临时称重操作员名
    private String username1;

    //交易称重操作员Id
    private String userid2;

    //交易称重操作员名
    private String username2;

    //临时称重班次
    private String bc1;

    //交易称重班次
    private String bc2;

    /**
     * 自动处理、先皮、先毛标识
     * 0 自动处理  1 先皮重  2 先毛重
     */
    private Integer scaleweightflag;

    //备用标识
    private Integer spareflag;

    /**
     * 上传标识'
     * 0 未上传 1 已上传
     */
    private Integer uploadflag;

    /**
     * 数据修改标志
     * 0 未修改  1 已经修改
     */
    private Integer dataeditflag;

    /**
     * 数据状态
     * 1 正常  9 已经删除 3 表示错误
     */
    private Integer datastatus;

    /**
     * 手工补单标志
     * 1 手工补单
     */
    private Integer manualinputflag;

    /**
     * 称重模式
     * 10 称重模式的代号
     */
    private Integer scalemode;

    /**
     * 多次称重结束的标志
     * 0 临时称重 1 交易称重
     */
    private Integer finalflag;

    //交易余额
    private Long leftweight;

    //皮重重量报警
    private Integer tareweightalarmflag;

    //皮重时间报警有效期
    private Integer taretimealarmflag;

    //称重时间间隔报警
    private Integer weighttimealarmflag;

    //自动保存标识 0 未自动保存 1 自动保存
    private Integer autosaveflag;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketno1() {
        return ticketno1;
    }

    public void setTicketno1(String ticketno1) {
        this.ticketno1 = ticketno1;
    }

    public String getTicketno2() {
        return ticketno2;
    }

    public void setTicketno2(String ticketno2) {
        this.ticketno2 = ticketno2;
    }

    public String getStation1() {
        return station1;
    }

    public void setStation1(String station1) {
        this.station1 = station1;
    }

    public String getStation2() {
        return station2;
    }

    public void setStation2(String station2) {
        this.station2 = station2;
    }

    public String getScaleno1() {
        return scaleno1;
    }

    public void setScaleno1(String scaleno1) {
        this.scaleno1 = scaleno1;
    }

    public String getScaleno2() {
        return scaleno2;
    }

    public void setScaleno2(String scaleno2) {
        this.scaleno2 = scaleno2;
    }

    public String getTruckno() {
        return truckno;
    }

    public void setTruckno(String truckno) {
        this.truckno = truckno;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public Calendar getFirstdatetime() {
        return firstdatetime;
    }

    public void setFirstdatetime(Calendar firstdatetime) {
        this.firstdatetime = firstdatetime;
    }

    public Calendar getSeconddatetime() {
        return seconddatetime;
    }

    public void setSeconddatetime(Calendar seconddatetime) {
        this.seconddatetime = seconddatetime;
    }

    public Calendar getGrossdatetime() {
        return grossdatetime;
    }

    public void setGrossdatetime(Calendar grossdatetime) {
        this.grossdatetime = grossdatetime;
    }

    public Calendar getTaredatetime() {
        return taredatetime;
    }

    public void setTaredatetime(Calendar taredatetime) {
        this.taredatetime = taredatetime;
    }

    public Integer getFirstweight() {
        return firstweight;
    }

    public void setFirstweight(Integer firstweight) {
        this.firstweight = firstweight;
    }

    public Integer getSecondweight() {
        return secondweight;
    }

    public void setSecondweight(Integer secondweight) {
        this.secondweight = secondweight;
    }

    public Integer getGross() {
        return gross;
    }

    public void setGross(Integer gross) {
        this.gross = gross;
    }

    public Integer getTare() {
        return tare;
    }

    public void setTare(Integer tare) {
        this.tare = tare;
    }

    public Integer getNet() {
        return net;
    }

    public void setNet(Integer net) {
        this.net = net;
    }

    public Integer getProductnet() {
        return productnet;
    }

    public void setProductnet(Integer productnet) {
        this.productnet = productnet;
    }

    public Integer getExceptwater() {
        return exceptwater;
    }

    public void setExceptwater(Integer exceptwater) {
        this.exceptwater = exceptwater;
    }

    public Integer getExceptother() {
        return exceptother;
    }

    public void setExceptother(Integer exceptother) {
        this.exceptother = exceptother;
    }

    public String getUserid1() {
        return userid1;
    }

    public void setUserid1(String userid1) {
        this.userid1 = userid1;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUserid2() {
        return userid2;
    }

    public void setUserid2(String userid2) {
        this.userid2 = userid2;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getBc1() {
        return bc1;
    }

    public void setBc1(String bc1) {
        this.bc1 = bc1;
    }

    public String getBc2() {
        return bc2;
    }

    public void setBc2(String bc2) {
        this.bc2 = bc2;
    }

    public Integer getScaleweightflag() {
        return scaleweightflag;
    }

    public void setScaleweightflag(Integer scaleweightflag) {
        this.scaleweightflag = scaleweightflag;
    }

    public Integer getSpareflag() {
        return spareflag;
    }

    public void setSpareflag(Integer spareflag) {
        this.spareflag = spareflag;
    }

    public Integer getUploadflag() {
        return uploadflag;
    }

    public void setUploadflag(Integer uploadflag) {
        this.uploadflag = uploadflag;
    }

    public Integer getDataeditflag() {
        return dataeditflag;
    }

    public void setDataeditflag(Integer dataeditflag) {
        this.dataeditflag = dataeditflag;
    }

    public Integer getDatastatus() {
        return datastatus;
    }

    public void setDatastatus(Integer datastatus) {
        this.datastatus = datastatus;
    }

    public Integer getManualinputflag() {
        return manualinputflag;
    }

    public void setManualinputflag(Integer manualinputflag) {
        this.manualinputflag = manualinputflag;
    }

    public Integer getScalemode() {
        return scalemode;
    }

    public void setScalemode(Integer scalemode) {
        this.scalemode = scalemode;
    }

    public Integer getFinalflag() {
        return finalflag;
    }

    public void setFinalflag(Integer finalflag) {
        this.finalflag = finalflag;
    }

    public Long getLeftweight() {
        return leftweight;
    }

    public void setLeftweight(Long leftweight) {
        this.leftweight = leftweight;
    }

    public Integer getTareweightalarmflag() {
        return tareweightalarmflag;
    }

    public void setTareweightalarmflag(Integer tareweightalarmflag) {
        this.tareweightalarmflag = tareweightalarmflag;
    }

    public Integer getTaretimealarmflag() {
        return taretimealarmflag;
    }

    public void setTaretimealarmflag(Integer taretimealarmflag) {
        this.taretimealarmflag = taretimealarmflag;
    }

    public Integer getWeighttimealarmflag() {
        return weighttimealarmflag;
    }

    public void setWeighttimealarmflag(Integer weighttimealarmflag) {
        this.weighttimealarmflag = weighttimealarmflag;
    }

    public Integer getAutosaveflag() {
        return autosaveflag;
    }

    public void setAutosaveflag(Integer autosaveflag) {
        this.autosaveflag = autosaveflag;
    }
}