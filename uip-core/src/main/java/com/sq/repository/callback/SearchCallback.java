package com.sq.repository.callback;

import com.sq.entity.search.Searchable;

import javax.persistence.Query;

/**
 * 查询回调接口.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 16:29
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface SearchCallback {

    public static final SearchCallback NONE = new NoneSearchCallback();
    public static final SearchCallback DEFAULT = new DefaultSearchCallback();


    /**
     * 动态拼HQL where、group by having
     *
     * @param ql
     * @param search
     */
    public void prepareQL(StringBuilder ql, Searchable search);

    public void prepareOrder(StringBuilder ql, Searchable search);

    /**
     * 根据search给query赋值及设置分页信息
     *
     * @param query
     * @param search
     */
    public void setValues(Query query, Searchable search);

    public void setPageable(Query query, Searchable search);
}

