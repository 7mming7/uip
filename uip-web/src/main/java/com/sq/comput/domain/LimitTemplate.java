package com.sq.comput.domain;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 指标限值配置模板.
 * User: shuiqing
 * Date: 2015/5/14
 * Time: 17:04
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name = "T_LimitTemplate")
public class LimitTemplate extends LimitBase {

    private static final long serialVersionUID = 3046421214102949920L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade= CascadeType.REFRESH, fetch= FetchType.LAZY)
    @JoinColumn(name="indicatorTempId")
    @org.hibernate.annotations.ForeignKey(name="fk_lt_indiTempId")
    @NotFound(action= NotFoundAction.IGNORE)
    private IndicatorTemp indicatorTemp;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public IndicatorTemp getIndicatorTemp() {
        return indicatorTemp;
    }

    public void setIndicatorTemp(IndicatorTemp indicatorTemp) {
        this.indicatorTemp = indicatorTemp;
    }
}
