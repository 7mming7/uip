package com.sq.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 抽象实体基类，提供统一的ID，和相关的基本功能方法
 * User: shuiqing
 * Date: 2015/2/19
 * Time: 14:48
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> extends AbstractEntity<ID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, precision=10)
    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }
}
