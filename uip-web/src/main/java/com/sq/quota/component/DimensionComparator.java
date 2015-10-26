package com.sq.quota.component;

import com.sq.quota.domain.QuotaTemp;

import java.util.Comparator;

/**
 * 指标排序比较器
 * User: shuiqing
 * Date: 2015/8/25
 * Time: 9:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class DimensionComparator implements Comparator<QuotaTemp> {
    @Override
    public int compare(QuotaTemp o1, QuotaTemp o2) {
        return o1.getCalFrequency() - o2.getCalFrequency();
    }
}
