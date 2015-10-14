package com.sq.quota.function.math;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 平均值函数，求出参数列表的平均值.
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
public class AvgFunction implements Function {

    public String getName() {
        return "avg";
    }

    /**
     * 求取参数列表的平均值
     * 2014年10月26日 下午11:40:22 ShuiQing PM 添加此方法
     * @param evaluator 表达式执行器
     * @param arguments 参数列表
     * @return 函数执行结果
     * @throws FunctionException
     */
    @SuppressWarnings("unchecked")
    public FunctionResult execute(Evaluator evaluator, String arguments)
            throws FunctionException {
        Double result = null;

        ArrayList<Double> numbers = FunctionHelper.getDoubles(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

        if (numbers.size() < 1) {
            throw new FunctionException("arguments -- " + arguments + " should more than one.");
        }

        try {
            Double total = 0d;
            for (Double num : numbers) {
                total = total + num;
            }
            BigDecimal bigDecimal = new BigDecimal(total/numbers.size());
            result = bigDecimal.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            throw new FunctionException("参数列表格式或数量出错!", e);
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}