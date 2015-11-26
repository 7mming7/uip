package com.sq.quota.domain;

import com.sq.entity.AbstractEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 指标重置配置记录
 * User: shuiqing
 * Date: 15/11/26
 * Time: 上午10:17
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "t_IndicatorResetData")
public class QuotaResetRecord extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false, precision=10)
    private Long id;

    /** 重置数据 */
    private Double resetValue;

    /** 重置日期 */
    private Calendar resetDate;

    /**
     *  关联的指标模板
     */
    @ManyToOne(cascade=CascadeType.REFRESH)
    @JoinColumn(name="indicatorTempId")
    @ForeignKey(name="fk_resetData_indicatorTempId")
    @NotFound(action= NotFoundAction.IGNORE)
    private QuotaTemp quotaTemp;

    /** 指标重置记录 */
    private Long indicatorResetConfigId;

    private Long creatorId;

    private Calendar createTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Double getResetValue() {
        return resetValue;
    }

    public void setResetValue(Double resetValue) {
        this.resetValue = resetValue;
    }

    public Calendar getResetDate() {
        return resetDate;
    }

    public void setResetDate(Calendar resetDate) {
        this.resetDate = resetDate;
    }

    public QuotaTemp getQuotaTemp() {
        return quotaTemp;
    }

    public void setQuotaTemp(QuotaTemp quotaTemp) {
        this.quotaTemp = quotaTemp;
    }

    public Long getIndicatorResetConfigId() {
        return indicatorResetConfigId;
    }

    public void setIndicatorResetConfigId(Long indicatorResetConfigId) {
        this.indicatorResetConfigId = indicatorResetConfigId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }
}
