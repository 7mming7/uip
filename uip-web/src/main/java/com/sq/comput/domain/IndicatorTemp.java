package com.sq.comput.domain;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 指标模板.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 9:31
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "t_IndicatorTemp")
public class IndicatorTemp extends IndicatorBase {

    private static final long serialVersionUID = -832754349061708675L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * 指标使用状态
     * 1---使用
     * 0---停用
     */
    private boolean indicatorStatus;

    /**
     * 指标计算公式
     */
    private String calculateExpression;

    /**
     * 指标创建时间
     */
    private Calendar createTime;

    /**
     * 序列
     */
    private int serialNo;

    @ManyToOne(cascade=CascadeType.REFRESH, fetch=FetchType.LAZY)
    @JoinColumn(name="categoryId")
    @ForeignKey(name="fk_it_categoryId")
    @NotFound(action= NotFoundAction.IGNORE)
    private IndicatorCategory indicatorCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIndicatorStatus() {
        return indicatorStatus;
    }

    public void setIndicatorStatus(boolean indicatorStatus) {
        this.indicatorStatus = indicatorStatus;
    }

    public String getCalculateExpression() {
        return calculateExpression;
    }

    public void setCalculateExpression(String calculateExpression) {
        this.calculateExpression = calculateExpression;
    }

    public IndicatorCategory getIndicatorCategory() {
        return indicatorCategory;
    }

    public void setIndicatorCategory(IndicatorCategory indicatorCategory) {
        this.indicatorCategory = indicatorCategory;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }
}
