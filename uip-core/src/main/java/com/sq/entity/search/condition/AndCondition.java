package com.sq.entity.search.condition;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * And 条件.
 * User: shuiqing
 * Date: 2015/3/17
 * Time: 15:54
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class AndCondition implements SearchFilter {

    private List<SearchFilter> andFilters = Lists.newArrayList();

    public AndCondition() {
    }

    public AndCondition add(SearchFilter filter) {
        this.andFilters.add(filter);
        return this;
    }

    public List<SearchFilter> getAndFilters() {
        return andFilters;
    }

    @Override
    public String toString() {
        return "AndCondition{" + andFilters + '}';
    }
}