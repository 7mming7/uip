package com.sq.quota.domain;

import com.sq.entity.AbstractEntity;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


/**
 * 指标类别.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 9:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name="t_IndicatorCategory")
public class QuotaCategory extends AbstractEntity<Long> {

    private static final long serialVersionUID = 1579016747893480431L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类等级
     */
    private int categoryLevel;

    @ManyToOne
    @JoinColumn(name="parentId")
    @ForeignKey(name="fk_indicator_parentId")
    private QuotaCategory parent;

    private boolean leaf;

    private int serialNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public QuotaCategory getParent() {
        return parent;
    }

    public void setParent(QuotaCategory parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
}
