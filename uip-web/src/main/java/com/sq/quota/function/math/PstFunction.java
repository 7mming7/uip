package com.sq.quota.function.math;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.util.ArrayList;

/**
 * pst函数，参数小于0记做0.
 * User: shuiqing
 * Date: 2015/5/25
 * Time: 10:19
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class PstFunction implements Function {


    public String getName() {
        return "pst";
    }

    /**
     * 求取参数
     *     小于0时计0
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

        if (numbers.size() != 1) {
            throw new FunctionException("arguments -- " + arguments + " should be one.");
        }

        try {
            if (numbers.get(0) < 0) {
                result = 0.0;
            } else {
                result = numbers.get(0);
            }
        } catch (Exception e) {
            throw new FunctionException("参数列表格式或数量出错!", e);
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}
