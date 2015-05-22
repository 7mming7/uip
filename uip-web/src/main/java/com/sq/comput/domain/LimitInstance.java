package com.sq.comput.domain;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 指标超限实例.
 * User: shuiqing
 * Date: 2015/5/14
 * Time: 17:15
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "T_LimitInstance")
public class LimitInstance extends LimitBase{

    private static final long serialVersionUID = -5178919816184494590L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Double limitValue;

    @ManyToOne(cascade= CascadeType.REFRESH, fetch= FetchType.LAZY)
    @JoinColumn(name="indicatorInstanceId")
    @org.hibernate.annotations.ForeignKey(name="fk_lt_indiInstanceId")
    @NotFound(action= NotFoundAction.IGNORE)
    private IndicatorInstance indicatorInstance;

    @ManyToOne(cascade= CascadeType.REFRESH, fetch= FetchType.LAZY)
    @JoinColumn(name="limitTemplateId")
    @org.hibernate.annotations.ForeignKey(name="fk_lt_limitTemplateId")
    @NotFound(action= NotFoundAction.IGNORE)
    private LimitTemplate limitTemplate;

    /** 是否超限 */
    private boolean isTransfinite;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LimitInstance(LimitTemplate limitTemplate){
        this.setAttribute(limitTemplate.getAttribute());
        this.setDescription(limitTemplate.getDescription());
        this.setLimitType(limitTemplate.getLimitType());
        this.setExpType(limitTemplate.getExpType());
        this.setExpression(limitTemplate.getExpression());
        this.setLimitTemplate(limitTemplate);
    }

    public Double getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Double limitValue) {
        this.limitValue = limitValue;
    }

    public IndicatorInstance getIndicatorInstance() {
        return indicatorInstance;
    }

    public void setIndicatorInstance(IndicatorInstance indicatorInstance) {
        this.indicatorInstance = indicatorInstance;
    }

    public boolean isTransfinite() {
        return isTransfinite;
    }

    public void setTransfinite(boolean isTransfinite) {
        this.isTransfinite = isTransfinite;
    }

    public LimitTemplate getLimitTemplate() {
        return limitTemplate;
    }

    public void setLimitTemplate(LimitTemplate limitTemplate) {
        this.limitTemplate = limitTemplate;
    }
}