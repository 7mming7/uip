package com.sq.protocol.jodbc.domain;

import com.sq.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 数据同步暂存记录(记录上次同步的截止值).
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 11:56
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Entity
@Table(name="T_Threshold")
public class Threshold extends AbstractEntity<Long> implements Serializable {

    private static final long serialVersionUID = -5686730782349433984L;

    /**
     * 唯一标示
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Calendar lastUpdateTime;

    private Long lastMaxValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Calendar lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getLastMaxValue() {
        return lastMaxValue;
    }

    public void setLastMaxValue(Long lastMaxValue) {
        this.lastMaxValue = lastMaxValue;
    }
}