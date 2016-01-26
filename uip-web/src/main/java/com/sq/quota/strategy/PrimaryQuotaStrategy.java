package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.SearchFilter;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.function.logical.LogicalFunctions;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.quota.repository.QuotaTempRepository;
import com.sq.quota.service.QuotaComputInsService;
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
import java.util.Date;
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

    private QuotaTempRepository quotaTempRepository = SpringUtils.getBean(QuotaTempRepository.class);

    private LogicalFunctions logicalFunctions = SpringUtils.getBean(LogicalFunctions.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(evaluator);

        String calculateExp = quotaTemp.getCalculateExpression();
        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty() || variableList.size() == 0) {
            log.error("表达式：" + calculateExp + " 没有动态参数!");
            return null;
        }

        long step1 = System.currentTimeMillis();

        log.error("step 0: ComputCal: " + DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_YMDH)
                + ", indicatorCode: " + quotaTemp.getIndicatorCode()
                + ", calculateExp: " + calculateExp
                + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression());

        //表达式计算的前置处理，将时间性的逻辑函数先做处理，然后再做数学计算
        calculateExp = parseExpressionFront(evaluator,
                variableList, calculateExp, DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULTYMDHMS));
        if(null == calculateExp) return null;

        long step2 = System.currentTimeMillis();

        log.error("step 1: ComputCal: " + DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_YMDH)
                + ", indicatorCode: " + quotaTemp.getIndicatorCode()
                + ", calculateExp: " + calculateExp
                + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression()
                + ", parseExpressionFront cost time: " + (step2 - step1));

        //动态参数替换
        calculateExp = dynamicVariableReplace(variableList,calculateExp,computCal,quotaTemp);

        long step3 = System.currentTimeMillis();

        log.error("step 2: ComputCal: " + DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_YMDH)
                + ", indicatorCode: " + quotaTemp.getIndicatorCode()
                + ", calculateExp: " + calculateExp
                + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression()
                + ", dynamicVariableReplace cost time: " + (step3 - step2));

        log.error("computCal->" + DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_YMDH)
                + ", quotaTemp->" + quotaTemp.getIndicatorCode()
                + ", calculateExp: " + calculateExp
                + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression());

        if (null == calculateExp) return null;

        Double result = null;
        try {
            result = Double.parseDouble(evaluator.evaluate(calculateExp));
        } catch (EvaluationException e) {
            log.error(" indicatorTemp->" + quotaTemp.getIndicatorName()
                    + "，indicatorCode->" + quotaTemp.getIndicatorCode()
                    + "，expression->" + quotaTemp.getCalculateExpression()
                    + "，GernaterdNativeExpression->" + quotaTemp.getGernaterdNativeExpression()
                    + "，computCal->" + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_YMDH)
                    + ", calculateExp->" + calculateExp, e);
        }
        return result;
    }

    /**
     * 动态参数替换
     * @param variableList 参数列表
     * @param calculateExp 计算表达式
     */
    public String dynamicVariableReplace (List<String> variableList, String calculateExp,
                                          Calendar computCal,QuotaTemp computQuotaTemp) {
        for (String variable : variableList) {
            Searchable searchable = Searchable.newSearchable()
                    .addSearchFilter("valueType", MatchType.EQ, QuotaConsts.VALUE_TYPE_DOUBLE);
            searchable.addSearchFilter("indicatorCode", MatchType.EQ, variable);

            QuotaTemp quotaTemp = quotaTempRepository.findByIndicatorCode(variable);
            if (null == quotaTemp) {
                log.error("Method dynamicVariableReplace error. QuotaTemp -> variable: " + variable + " is not exist!");
            }
            searchable = fillSearchConditionByFetchType(searchable,quotaTemp,computQuotaTemp,computCal);
            List<QuotaInstance> quotaInstances = quotaInstanceRepository.findAll(searchable).getContent();
            for (SearchFilter searchFilter:searchable.getSearchFilters()) {
                log.error("SearchFilter:quotaTemp->" + quotaTemp.getIndicatorCode() + "," + searchFilter.toString());
            }

            StringBuilder variableBuilder = new StringBuilder();
            long whileStartMillions = System.currentTimeMillis();
            long computMillions = System.currentTimeMillis();
            while (quotaInstances.isEmpty() && computMillions<=QuotaComputHelper.requestWaitTimeOutValue*1000) {
                computMillions = System.currentTimeMillis() - whileStartMillions;
                try {
                    Thread.sleep(10l);
                } catch (InterruptedException e) {
                    log.error("Thread sleep error.", e);
                }
                quotaInstances = quotaInstanceRepository.findAll(searchable).getContent();
                log.debug("While search variable : " + variable);
            }
            List<QuotaInstance> quotaDyList = new ArrayList<QuotaInstance>(quotaInstances);

            List<QuotaInstance> nullValueQuotaInstanceList = new ArrayList<QuotaInstance>();
            for (QuotaInstance quotaInstance:quotaDyList) {
                if (null == quotaInstance.getFloatValue()) {
                    log.error("quotaInstance floatvalue null: " + quotaInstance.getIndicatorCode()
                            + ",statDateNum:" + quotaInstance.getStatDateNum());
                    nullValueQuotaInstanceList.add(quotaInstance);
                }
            }
            if(!nullValueQuotaInstanceList.isEmpty()) {
                quotaDyList.removeAll(nullValueQuotaInstanceList);
            }

            if (quotaDyList.isEmpty()) {
                log.error("QuotaTemp -> variable: " + variable + " exist no instance!");
                if (quotaTemp.getDoWithNull() == QuotaConsts.DOWITH_NULL_BENULL) {
                    return null;
                } else {
                    variableBuilder.append("0");
                }
            }

            if (quotaDyList.size() >= 1) {
                for (QuotaInstance indicatorInstance : quotaDyList) {
                    log.error("Comput quotaTemp: " + quotaTemp.getIndicatorCode()
                            + ",quotaDyList code: " + indicatorInstance.getIndicatorCode()
                            + ",value: " + indicatorInstance.getFloatValue()
                            + ",computCal: " + indicatorInstance.getStatDateNum());
                    String itemValue = indicatorInstance.getFloatValue().toString();
                    variableBuilder.append(itemValue).append(",");
                }
                variableBuilder.deleteCharAt(variableBuilder.lastIndexOf(","));

            }
            String replaceVariable = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;
            calculateExp = calculateExp.replace(replaceVariable, variableBuilder.toString());
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
                    String variableTemp = new String(variable);
                    needDeleteVariableList.add(variable);
                    variable = variable.replace(")", "," + computCal + ")");

                    String result = "";
                    try {
                        result = evaluator.evaluate(variable);
                    } catch (EvaluationException e) {
                        log.error("parseExpressionFront -> logicalFunctions 指标计算出现错误.calculateExp: " + calculateExp, e);
                    }
                    if (result.equals("'null'")) {
                        List<String> subVariableList = QuotaComputHelper.getVariableList(variable,evaluator);
                        QuotaTemp assQuotaTemp = new QuotaTemp();
                        for (String assCode:subVariableList) {
                            QuotaTemp quotaTemp = quotaTempRepository.findByIndicatorCode(assCode);
                            if (null != assQuotaTemp)
                                assQuotaTemp = quotaTemp;
                        }
                        log.error("assQuotaTemp :" + assQuotaTemp.getIndicatorCode()
                                + "calculateExp : " + calculateExp
                                + ", ParseExpressionFront result: " + result);
                        if (assQuotaTemp.getDoWithNull() == QuotaConsts.DOWITH_NULL_BENULL) {
                            return null;
                        } else {
                            result = "'0'";
                        }
                    }
                    result = result.substring(1,result.length() - 1);
                    String replaceVariable = EvaluationConstants.OPEN_VARIABLE + variableTemp + EvaluationConstants.CLOSED_BRACE;
                    calculateExp = calculateExp.replace(replaceVariable, result);
                }
            }
        }
        variableList.removeAll(needDeleteVariableList);
        return calculateExp;
    }
}
