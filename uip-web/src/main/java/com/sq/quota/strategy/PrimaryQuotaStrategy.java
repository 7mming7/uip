package com.sq.quota.strategy;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * 元指标计算策略.
 * User: shuiqing
 * Date: 2015/4/22
 * Time: 13:25
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class PrimaryQuotaStrategy extends IQuotaComputStrategy {

    private Logger log = LoggerFactory.getLogger(PrimaryQuotaStrategy.class);

    private QuotaInstanceRepository quotaInstanceRepository = SpringUtils.getBean(QuotaInstanceRepository.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        String calculateExp = quotaTemp.getCalculateExpression();
        List<String> variableList = ComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty()) {
            log.error("表达式：" + calculateExp + " 没有动态参数!");
            return null;
        }

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("valueType", MatchType.EQ, IndicatorConsts.VALUE_TYPE_DOUBLE);
        int fetchCycle = quotaTemp.getFetchCycle();
        searchable = fillSearchConditionByFetchType(searchable,fetchCycle,computCal);
        for (String variable : variableList) {
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);
            List<IndicatorInstance> indicatorInstances = quotaInstanceRepository.findAll(searchable).getContent();

            if (indicatorInstances.isEmpty()) {
                log.error(searchable.toString() + "计算指标：" + quotaTemp.getIndicatorCode() + "-》关联指标：" + variable + " 没有数据!默认为0.");
                evaluator.putVariable(variable, "0");
            }

            if (indicatorInstances.size() >= 1) {
                StringBuilder variableBuilder = new StringBuilder();
                for (IndicatorInstance indicatorInstance : indicatorInstances) {
                    String itemValue = indicatorInstance.getFloatValue().toString();
                    variableBuilder.append(itemValue).append(",");
                }
                variableBuilder.deleteCharAt(variableBuilder.lastIndexOf(","));
                String replaceVariable = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;

                calculateExp = calculateExp.replace(replaceVariable, variableBuilder.toString());
            }
            searchable.removeSearchFilter("indicatorCode", MatchType.EQ);
        }

        Double result = null;
        try {
            result = Double.valueOf(evaluator.evaluate(calculateExp));
        } catch (EvaluationException e) {
            log.error("indicatorTemp->" + quotaTemp.getIndicatorName()
                    + "，computCal->" + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULT)
                    + ",计算结果cast to double error. calculateExp->" + calculateExp, e);
        }
        return result;
    }
}
