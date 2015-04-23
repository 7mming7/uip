package com.sq.comput.strategy;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
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
public class PrimaryStrategy extends IComputStrategy {

    private Logger log = LoggerFactory.getLogger(PrimaryStrategy.class);

    private IndicatorInstanceRepository indicatorInstanceRepository = SpringUtils.getBean(IndicatorInstanceRepository.class);

    @Override
    public Object execIndiComput(IndicatorTemp indicatorTemp, Calendar computCal) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        String calculateExp = indicatorTemp.getCalculateExpression();
        List<String> variableList = ComputHelper.getVariableList(calculateExp,evaluator);
        Assert.notEmpty(variableList, "表达式：" + calculateExp + " 没有动态参数!");

        Searchable searchable = Searchable.newSearchable();
        int fetchCycle = indicatorTemp.getFetchCycle();
        fillSearchConditionByFetchType(searchable,fetchCycle,computCal);

        for (String variable : variableList) {
            searchable.addSearchFilter("itemCode", MatchType.EQ, variable);
            List<IndicatorInstance> indicatorInstances = indicatorInstanceRepository.findAll(searchable).getContent();

            Assert.notEmpty(indicatorInstances, "关联指标：" + variable + " 没有数据!");

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
            searchable.removeSearchFilter("itemCode", MatchType.EQ);
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
