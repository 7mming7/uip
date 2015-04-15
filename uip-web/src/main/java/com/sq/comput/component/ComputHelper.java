package com.sq.comput.component;

import com.sq.comput.function.AvgFunction;
import com.sq.comput.function.MaxAllFunction;
import com.sq.comput.function.SubFunction;
import com.sq.comput.function.SumFunction;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationHelper;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标计算辅助类.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 10:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class ComputHelper {

    private static final Logger log = LoggerFactory.getLogger(ComputHelper.class);

    private static Evaluator evaluator = null;

    public static Evaluator getEvaluatorInstance () {
        if (null == evaluator ) {
            evaluator = new Evaluator();
        }
        return evaluator;
    }

    static {
        loadLocalFunctions(getEvaluatorInstance());
    }

    /**
     * 加载本地函数
     * 2014年10月27日 下午2:32:50 ShuiQing PM 添加此方法
     * @param evaluator 表达式执行器
     */
    private static void loadLocalFunctions (Evaluator evaluator) {
        evaluator.putFunction(new AvgFunction());
        evaluator.putFunction(new MaxAllFunction());
        evaluator.putFunction(new SubFunction());
        evaluator.putFunction(new SumFunction());
    }

    /**
     * 搜索出表达式中中配置的动态参数
     * 2014年10月26日 下午10:12:53 ShuiQing PM 添加此方法
     * @param expression 表达式copy
     * @param evaluator 表达式执行器
     * @return 动态参数列表
     */
    public static List<String> getVariableList (String expression,Evaluator evaluator) {
        List<String> variableList = new ArrayList<String>();
        int openIndex = expression.indexOf(EvaluationConstants.OPEN_VARIABLE);

        if (openIndex < 0) {
            return variableList;
        }

        String replacedExpression = expression;

        while (openIndex >= 0) {

            int closedIndex = -1;
            if (openIndex >= 0) {
                closedIndex = replacedExpression.indexOf(
                        EvaluationConstants.CLOSED_VARIABLE, openIndex + 1);
                if (closedIndex > openIndex) {

                    String variableName = replacedExpression.substring(
                            openIndex
                                    + EvaluationConstants.OPEN_VARIABLE
                                    .length(), closedIndex);

                    // Validate that the variable name is valid.
                    try {
                        evaluator.isValidName(variableName);
                    } catch (IllegalArgumentException iae) {
                        String msg = "获取动态参数失败,Invalid variable name of \"" + variableName + "\".";
                        log.error(msg, iae);
                    }
                    variableList.add(variableName);

                    String variableString = EvaluationConstants.OPEN_VARIABLE
                            + variableName
                            + EvaluationConstants.CLOSED_VARIABLE;

                    replacedExpression = EvaluationHelper.replaceAll(
                            replacedExpression, variableString, "");
                } else {
                    break;
                }
            }

            openIndex = replacedExpression.indexOf(
                    EvaluationConstants.OPEN_VARIABLE);
        }
        return variableList;
    }
}
