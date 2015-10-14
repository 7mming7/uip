package com.sq.quota.function.math;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 减法函数，第二个参数和第一个参数的差值.
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
public class SubFunction implements Function {

    public String getName() {
        return "sub";
    }

    @SuppressWarnings("unchecked")
    public FunctionResult execute(final Evaluator evaluator, final String arguments)
            throws FunctionException {
        Double result = null;

        ArrayList<Double> numbers = FunctionHelper.getDoubles(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

        if (numbers.size() < 2) {
            throw new FunctionException("arguments should more than two.");
        }

        try {
            double argumentOne = ((Double) numbers.get(0)).doubleValue();
            double argumentTwo = ((Double) numbers.get(numbers.size()-1)).doubleValue();
            BigDecimal bigDecimal = new BigDecimal(argumentTwo - argumentOne);
            result = bigDecimal.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            throw new FunctionException("参数列表格式或数量出错!", e);
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}