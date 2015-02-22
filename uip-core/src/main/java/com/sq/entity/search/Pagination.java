package com.sq.entity.search;

import com.sq.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息对象。默认情况下认为需要分页并排序.
 * 如果不需要分页，需要调用setNeedsPaginate()方法.
 * 如果不需要排序，需要调用setNeedsSort()方法.
 * User: shuiqing
 * Date: 2015/2/21
 * Time: 16:39
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class Pagination {

    // ==========================================
    // fields...
    /** 标记是否分页*/
    private boolean needsPaginate;

    /** 标记是否排序*/
    private boolean needsSort;

    /** 分页的启示记录序号*/
    private int startPos;

    /** 每页取出的记录数*/
    private int amount = Constants.DEFAULT_PAGE_SIZE;

    /** 需要排序的属性*/
    private List<String> sortPropertyList;

    /**
     * 是否正向排序,默认为true
     */
    private boolean asc = true;

    // ==========================================
    // constructor...

    public Pagination() {
        this.needsPaginate = false;
        this.needsSort = false;
        this.startPos = -1;
    }

    public Pagination(boolean needsSort) {
        this.needsSort = needsSort;
        this.startPos = -1;
        this.needsPaginate = false;
    }

    public Pagination(boolean needsSort, boolean asc) {
        List<String> sortPropertyList = new ArrayList<String>();
        sortPropertyList.add("id");
        this.needsSort = needsSort;
        this.sortPropertyList = sortPropertyList;
        this.asc = asc;
        this.startPos = -1;
    }

    public Pagination(boolean needsSort, List<String> sortPropertyList, boolean asc) {
        this.needsSort = needsSort;
        this.sortPropertyList = sortPropertyList;
        this.asc = asc;
        this.startPos = -1;
    }

    public Pagination(boolean needsPaginate, boolean needsSort, List<String> sortPropertyList, boolean asc) {
        this.needsPaginate = needsPaginate;
        this.needsSort = needsSort;
        this.sortPropertyList = sortPropertyList;
        this.asc = asc;
        this.startPos = -1;
    }

    // ==========================================
    // set get method...

    public boolean isNeedsPaginate() {
        return needsPaginate;
    }

    public void setNeedsPaginate(boolean needsPaginate) {
        this.needsPaginate = needsPaginate;
    }

    public boolean isNeedsSort() {
        return needsSort;
    }

    public void setNeedsSort(boolean needsSort) {
        this.needsSort = needsSort;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getSortPropertyList() {
        return sortPropertyList;
    }

    public void setSortPropertyList(List<String> sortPropertyList) {
        this.sortPropertyList = sortPropertyList;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
