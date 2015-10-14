package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.function.logical.LogicalFunctions;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    private LogicalFunctions logicalFunctions = SpringUtils.getBean(LogicalFunctions.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(evaluator);

        String calculateExp = quotaTemp.getCalculateExpression();
        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty()) {
            log.error("表达式：" + calculateExp + " 没有动态参数!");
            return null;
        }

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("valueType", MatchType.EQ, QuotaConsts.VALUE_TYPE_DOUBLE);
        int fetchCycle = quotaTemp.getFetchCycle();
        searchable = fillSearchConditionByFetchType(searchable,fetchCycle,computCal);

        calculateExp = dynamicVariableReplace(variableList,calculateExp,searchable);

        Double result = null;
        try {
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

    /**
     * 动态参数替换
     * @param variableList 参数列表
     * @param calculateExp 计算表达式
     */
    public String dynamicVariableReplace (List<String> variableList, String calculateExp, Searchable searchable) {
        for (String variable : variableList) {
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);
            searchable.addSearchFilter("floatValue", MatchType.isNotNull, "");
            List<QuotaInstance> quotaInstances = quotaInstanceRepository.findAll(searchable).getContent();

            if (quotaInstances.isEmpty()) {
                return null;
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
        return calculateExp;
    }

    /**
     * 前置解析计算表达式
     *    主要是处理涉及逻辑操作上的处理
     * @param variableList 参数列表
     * @param calculateExp 计算表达式
     */
    public String parseExpressionFront (Evaluator evaluator, List<String> variableList, String calculateExp, String computCal) {
        List<Function> functionList = logicalFunctions.getFunctions();

        List<String> needDeleteVariableList = new ArrayList<String>();
        for (String variable:variableList) {
            for (Function function:functionList) {
                if (variable.startsWith(function.getName())) {
                    needDeleteVariableList.add(variable);
                    variable.replace(")", "," + computCal + ")");
                    String result = "";
                    try {
                        result = evaluator.evaluate(variable);
                    } catch (EvaluationException e) {
                        log.error("DateTime 指标计算出现错误.");
                    }
                    String replaceVariable = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;
                    calculateExp = calculateExp.replace(replaceVariable, result);
                }
            }
        }
        variableList.removeAll(needDeleteVariableList);
        return calculateExp;
    }
}
