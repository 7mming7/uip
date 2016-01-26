package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.component.QuotaBaseConfigure;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaTemp;
import com.sq.util.DateUtil;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

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
public abstract class IQuotaComputStrategy {

    private static final Logger log = LoggerFactory.getLogger(IQuotaComputStrategy.class);

    /** 执行指标计算->根据不同的实现返回计算结果 */
    public abstract Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal);

    /**
     * 校验计算表达式的正确性
     * @return 是否有效
     */
    public static boolean checkQuotaExpression (Evaluator evaluator, QuotaTemp quotaTemp) {
        List<String> variableList = QuotaComputHelper.getVariableList(quotaTemp.getCalculateExpression(), evaluator);
        if (variableList.size() == 0 || variableList.isEmpty()) {
            log.error("指标：" + quotaTemp.getIndicatorCode() + "-的表达式没有动态参数!");
            return false;
        }

        return true;
    }

    /**
     * 根据指标的取数频率拼装查询条件
     * @param searchable      查询条件
     * @param quotaTemp       参数指标
     * @param computQuotaTemp 计算指标
     * @param computCal       输入时间
     * @return 拼装的查询条件
     */
    public static Searchable fillSearchConditionByFetchType (Searchable searchable, QuotaTemp quotaTemp,
                                                             QuotaTemp computQuotaTemp, Calendar computCal) {
        Assert.notNull(searchable, "searchable can not be null!");
        int[] dayArray = new int[2];
        int switchParam = computQuotaTemp.getFetchCycle();
        log.error("computQuotaTemp1:" + computQuotaTemp.getIndicatorCode()
                + ",varQuotaTemp:" + quotaTemp.getIndicatorCode()
                + ",comput fetchCycle:" + computQuotaTemp.getFetchCycle()
                + ",quotaTemp fetchCycle:" + quotaTemp.getFetchCycle()
                + ",switchParam:" + switchParam);
        if (computQuotaTemp.getFetchCycle() == quotaTemp.getFetchCycle()) {
            switchParam = computQuotaTemp.getCalFrequency();
        }
        log.error("computQuotaTemp2:" + computQuotaTemp.getIndicatorCode()
                + ",varQuotaTemp:" + quotaTemp.getIndicatorCode()
                + ",comput fetchCycle:" + computQuotaTemp.getFetchCycle()
                + ",quotaTemp fetchCycle:" + quotaTemp.getFetchCycle()
                + ",switchParam:" + switchParam);
        switch (switchParam) {
            case QuotaConsts.FETCH_CYCLE_HALF_HOUR:
                searchable.addSearchFilter("instanceTime", MatchType.EQ, computCal);
                break;
            case QuotaConsts.FETCH_CYCLE_HOUR:
                searchable.addSearchFilter("instanceTime", MatchType.EQ, computCal);
                break;
            case QuotaConsts.FETCH_CYCLE_DAY:
                if (quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_INTERFACE) {
                    Calendar[] dayCalArray = DateUtil.getPointDayFirstAndLast(
                            computCal,
                            QuotaBaseConfigure.startHour,
                            QuotaBaseConfigure.endHour);
                    searchable.addSearchFilter("instanceTime", MatchType.GTE, dayCalArray[0]);
                    searchable.addSearchFilter("instanceTime", MatchType.LT, dayCalArray[1]);
                } else if (quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_CALCULATE
                        || quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_ENTRY ) {
                    dayArray = DateUtil.getDayFirstAndLastInt(computCal);
                    searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                    searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
                }
                break;
            case QuotaConsts.FETCH_CYCLE_WEEK:
                dayArray = DateUtil.getWeekFirstAndInputInt(computCal);
                searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
                break;
            case QuotaConsts.FETCH_CYCLE_Month:
                dayArray = DateUtil.getMonthFirstAndInputInt(computCal);
                searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
                break;
            case QuotaConsts.FETCH_CYCLE_Quarter:
                dayArray = DateUtil.getQuarterFirstAndInputInt(computCal);
                searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
                break;
            case QuotaConsts.FETCH_CYCLE_Year:
                dayArray = DateUtil.getYearFirstAndInputInt(computCal);
                searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
                break;
            default:
                dayArray = DateUtil.getDayFirstAndLastInt(computCal);
                searchable.addSearchFilter("statDateNum", MatchType.GTE, dayArray[0]);
                searchable.addSearchFilter("statDateNum", MatchType.LTE, dayArray[1]);
        }
        return searchable;
    }

    /**
     * 计算前置的分钟数
     */
    public static Integer calPreMinutes(QuotaTemp quotaTemp){
        Integer preMinute = null;
        int switchCycle = quotaTemp.getFetchCycle();
        switch (switchCycle) {
            case QuotaConsts.FETCH_CYCLE_HALF_HOUR:
                preMinute = 30;
                break;
            case QuotaConsts.FETCH_CYCLE_HOUR:
                preMinute = 60;
                break;
            default:
                preMinute = 60;
        }
        return preMinute;
    }
}
