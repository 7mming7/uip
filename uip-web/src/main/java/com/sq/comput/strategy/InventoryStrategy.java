package com.sq.comput.strategy;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
public class InventoryStrategy extends IComputStrategy {

    private Logger log = LoggerFactory.getLogger(InventoryStrategy.class);

    private IndicatorInstanceRepository indicatorInstanceRepository = SpringUtils.getBean(IndicatorInstanceRepository.class);

    @Override
    public Object execIndiComput(IndicatorTemp indicatorTemp, Calendar computCal) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        String calculateExp = indicatorTemp.getCalculateExpression();
        List<String> variableList = ComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty()) {
            log.error("表达式：" + calculateExp + " 没有动态参数!");
        }

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("valueType", MatchType.EQ, IndicatorConsts.VALUE_TYPE_DOUBLE);
        int fetchCycle = indicatorTemp.getFetchCycle();
        fillSearchConditionByFetchType(searchable, fetchCycle, computCal);

        for (String variable : variableList) {
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);

            if (variable.equals(indicatorTemp.getIndicatorCode())) {
                searchable.addSearchFilter("statDateNum", MatchType.EQ, DateUtil.getPreDay(computCal));
                IndicatorInstance indicatorInstance = indicatorInstanceRepository.findAll(searchable).getContent().get(0);
                evaluator.putVariable(variable, indicatorInstance.getFloatValue().toString());
                break;
            }

            List<IndicatorInstance> indicatorInstances = indicatorInstanceRepository.findAll(searchable).getContent();

            if (indicatorInstances.isEmpty()) {
                log.error("关联指标：" + variable + " 没有数据!");
                return "";
            }

            if (indicatorInstances.size() == 1) {
                evaluator.putVariable(variable, indicatorInstances.get(0).getFloatValue().toString());
            }

            if (indicatorInstances.size() > 1) {
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
            e.printStackTrace();
        }
        return result;
    }
}
