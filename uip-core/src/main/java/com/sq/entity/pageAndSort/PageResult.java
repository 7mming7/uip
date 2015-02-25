package com.sq.entity.pageAndSort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 抽象返回到前台的页面对象.
 * User: shuiqing
 * Date: 2015/2/20
 * Time: 16:36
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 5630518079179620343L;

    // fields...
    /** 当前第几页，默认值为1，既第一页. */
    private int pageNo = 1;

    /** 查询结果. */
    private Object result;

    /** 当前查询结果是否成功. */
    private boolean success;

    /** 当前查询结果返回前台的消息 */
    private String message;

    /** 总记录数，默认值为-1，表示totalCount不可用. */
    private long totalCount = -1L;

    /** 是否计算数据库中的记录总数. */
    private boolean autoCount;

    /** 总页数，默认值为-1，表示pageCount不可用. */
    private long pageCount = -1;

    // ==========================================
    // constructor...
    /** 构造方法. */
    public PageResult() {
        totalCount = 0;
        result = new ArrayList();
    }

    public PageResult(Object result, long totalCount) {
        this.result = result;
        this.totalCount = totalCount;
    }

    // ==========================================
    // 工具方法
    /**
     * 是否有前一页.
     *
     * @return boolean
     */
    public boolean isPreviousEnabled() {
        return pageNo > 1;
    }

    /**
     * 是否有后一页.
     *
     * @return boolean
     */
    public boolean isNextEnabled() {
        return pageNo < pageCount;
    }

    /**
     * 总页数是否可用.
     *
     * @return boolean
     */
    public boolean isPageCountEnabled() {
        return pageCount >= 0;
    }

    // ==========================================
    // getter and setter method...
    public int getPageNo() {
        return pageNo;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public boolean isAutoCount() {
        return autoCount;
    }

    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultSize() {
        if (result instanceof Collection) {
            return ((Collection) result).size();
        } else {
            return 0;
        }
    }
}
