package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
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
        Evaluator evaluator = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(evaluator);
        String calculateExp = quotaTemp.getGernaterdNativeExpression();
        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty()) {
            log.error("表达式：" + calculateExp + " 没有动态参数!");
            return null;
        }

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("valueType", MatchType.EQ, QuotaConsts.VALUE_TYPE_DOUBLE);
        int fetchCycle = quotaTemp.getFetchCycle();
        searchable = fillSearchConditionByFetchType(searchable,fetchCycle,computCal);
        for (String variable : variableList) {
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);
            searchable.addSearchFilter("floatValue", MatchType.isNotNull, "");
            List<QuotaInstance> quotaInstances = quotaInstanceRepository.findAll(searchable).getContent();

            if (quotaInstances.isEmpty()) {
                evaluator.putVariable(variable, "0");
            }

            if (quotaInstances.size() >= 1) {
                StringBuilder variableBuilder = new StringBuilder();
                for (QuotaInstance indicatorInstance : quotaInstances) {
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
            System.out.println(calculateExp);
            result = Double.parseDouble(evaluator.evaluate(calculateExp));
        } catch (EvaluationException e) {
            log.error("/n indicatorTemp->" + quotaTemp.getIndicatorName()
                    + "，indicatorCode->" + quotaTemp.getIndicatorCode()
                    + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression()
                    + "，computCal->" + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULT)
                    + ", calculateExp->" + calculateExp, e);
        }
        return result;
    }
}
