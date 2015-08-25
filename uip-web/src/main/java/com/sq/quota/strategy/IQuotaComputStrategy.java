package com.sq.quota.strategy;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.component.QuotaComputHelper;
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
     * @param expression 指标表达式
     * @return 是否有效
     */
    public static boolean checkQuotaExpression (Evaluator evaluator, QuotaTemp quotaTemp) {
        List<String> variableList = QuotaComputHelper.getVariableList(quotaTemp.getCalculateExpression(), evaluator);
        if (variableList.isEmpty()) {
            log.error("指标：" + quotaTemp.getIndicatorCode() + "-的表达式没有动态参数!");
            return false;
        }

        return true;
    }

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
}
