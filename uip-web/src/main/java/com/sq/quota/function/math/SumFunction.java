package com.sq.quota.function.math;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.util.ArrayList;

/**
 * 求和函数，对参数列表求和.
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
public class SumFunction implements Function {

    public String getName() {
        return "sum";
    }

    /**
     * 在参数列表中获取最大值
     * 2014年10月26日 下午11:40:22 ShuiQing PM 添加此方法
     * @param evaluator 表达式执行器
     * @param arguments 参数列表
     * @return 函数执行结果
     * @throws FunctionException
     */
    @SuppressWarnings("unchecked")
    public FunctionResult execute(Evaluator evaluator, String arguments)
            throws FunctionException {
        Double result = 0d;

        ArrayList<Double> numbers = FunctionHelper.getDoubles(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

        try {
            for (double num : numbers) {
                result += num;
            }
        } catch (Exception e) {
            throw new FunctionException("参数列表格式或数量出错!", e);
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}