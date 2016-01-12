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
 * 库存指标计算策略.
 * User: shuiqing
 * Date: 2015/4/23
 * Time: 17:38
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class InventoryQuotaStrategy extends IQuotaComputStrategy {

    private Logger log = LoggerFactory.getLogger(InventoryQuotaStrategy.class);

    private QuotaInstanceRepository quotaInstanceRepository = SpringUtils.getBean(QuotaInstanceRepository.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(evaluator);
        String calculateExp = quotaTemp.getGernaterdNativeExpression();
        if (!checkQuotaExpression(evaluator,quotaTemp)) {
            return null;
        }

        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("valueType", MatchType.EQ, QuotaConsts.VALUE_TYPE_DOUBLE);

        for (String variable : variableList) {

            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);
            searchable.addSearchFilter("statDateNum", MatchType.EQ, DateUtil.getCurrDay(computCal));
            searchable.addSearchFilter("floatValue", MatchType.isNotNull, "");
            List<QuotaInstance> quotaInstances = quotaInstanceRepository.findAll(searchable).getContent();

            if (quotaInstances.size() == 1) {
                evaluator.putVariable(variable, quotaInstances.get(0).getFloatValue().toString());
            }

            if (quotaInstances.size() > 1) {
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
            searchable.removeSearchFilter("statDateNum", MatchType.EQ);
        }

        Double result = null;
        try {
            result = Double.parseDouble(evaluator.evaluate(calculateExp));
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, quotaTemp.getIndicatorCode());
            searchable.addSearchFilter("statDateNum", MatchType.EQ, DateUtil.getPreDay(computCal));
            if (!quotaInstanceRepository.findAll(searchable).getContent().isEmpty()
                    && quotaInstanceRepository.findAll(searchable).getContent().get(0).getValueType() == QuotaConsts.VALUE_TYPE_DOUBLE) {
                result = result + quotaInstanceRepository.findAll(searchable).getContent().get(0).getFloatValue();
            }
        } catch (EvaluationException e) {
            log.error("/n indicatorTemp->" + quotaTemp.getIndicatorName()
                    + "，indicatorCode->" + quotaTemp.getIndicatorCode()
                    + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression()
                    + "，computCal->" + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULT)
                    + ",计算结果cast to double error. calculateExp->" + calculateExp, e);
        }
        return result;
    }
}
