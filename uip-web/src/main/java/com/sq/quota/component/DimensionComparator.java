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
        if (o1.getCalFrequency() > o2.getCalFrequency()) {
            return o1.getCalFrequency() - o2.getCalFrequency();
        } else if (o1.getCalFrequency() == o2.getCalFrequency()
                && o1.getFetchCycle() > o2.getFetchCycle()) {
            return o1.getFetchCycle() - o2.getFetchCycle();
        } else if (o1.getCalFrequency() == o2.getCalFrequency()
                && o1.getFetchCycle() == o2.getFetchCycle()
                && o1.getSemaphore() >= o2.getSemaphore()) {
            return o1.getSemaphore() - o2.getSemaphore();
        } else {
            return -1;
        }
    }
}
