package com.sq.comput.strategy;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaTemp;
import com.sq.util.DateUtil;
import org.springframework.util.Assert;

import java.util.Calendar;

/**
 * 指标计算策略.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public abstract class IComputStrategy {

    /** 执行指标计算->根据不同的实现返回计算结果 */
    public abstract Object execIndiComput(IndicatorTemp indicatorTemp, Calendar computCal);

    /**
     * 根据指标的取数频率拼装查询条件
     * @param searchable  查询条件
     * @param fetchCycle  取数频率
     * @param computCal   输入时间
     * @return 拼装的查询条件
     */
    public static Searchable fillSearchConditionByFetchType (Searchable searchable, int fetchCycle, Calendar computCal) {
        Assert.notNull(searchable, "searchable can not be null!");
        int[] dayArray = new int[2];
        switch (fetchCycle) {
            case IndicatorConsts.FETCH_CYCLE_DAY:
                dayArray = DateUtil.getDayFirstAndLastInt(computCal);
                break;
            case IndicatorConsts.FETCH_CYCLE_WEEK:
                dayArray = DateUtil.getWeekFirstAndInputInt(computCal);
                break;
            case IndicatorConsts.FETCH_CYCLE_Month:
                dayArray = DateUtil.getMonthFirstAndInputInt(computCal);
                break;
            case IndicatorConsts.FETCH_CYCLE_Quarter:
                dayArray = DateUtil.getQuarterFirstAndInputInt(computCal);
                break;
            case IndicatorConsts.FETCH_CYCLE_Year:
                dayArray = DateUtil.getYearFirstAndInputInt(computCal);
                break;
        }
        searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
        searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
        return searchable;
    }

    /**
     * 计算前置的分钟数
     */
    public static Long calPreMinutes(IndicatorTemp indicatorTemp){
        Long preMinute = null;
        int switchCycle = indicatorTemp.getFetchCycle();
        switch (switchCycle) {
            case QuotaConsts.FETCH_CYCLE_HALF_HOUR:
                preMinute = 30l;
                break;
            case QuotaConsts.FETCH_CYCLE_HOUR:
                preMinute = 60l;
                break;
            default:
                preMinute = 60l;
        }
        return preMinute;
    }
}
