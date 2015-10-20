package com.sq.quota.function.logical;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.util.ArrayList;

import com.sq.util.DateUtil;

/**
 * 日期函数
 * User: shuiqing
 * Date: 2015/8/25
 * Time: 9:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class DateTimeFunction implements Function {

    private QuotaInstanceRepository quotaInstanceRepository = SpringUtils.getBean(QuotaInstanceRepository.class);

    public String getName() {
        return "dateTime";
    }

    @Override
    public FunctionResult execute(Evaluator evaluator, String arguments)
            throws FunctionException {
        Double result = null;
        ArrayList<String> argList = FunctionHelper.getStrings(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

        String cycleStr = argList.get(0);

        String disDateNum = argList.get(1);

        Integer disDateInt = Integer.parseInt(disDateNum);

        String assQuotaCode = argList.get(2);

        String computCal = argList.get(3);

        //计算指标的迁移之后的日期
        String assComputCal = DateUtil.dateMigrate(cycleStr, disDateInt, computCal);
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorCode", MatchType.EQ, assQuotaCode)
                .addSearchFilter("statDateNum", MatchType.EQ, assComputCal);

        QuotaInstance quotaInstance = quotaInstanceRepository.findAll(searchable).getContent().get(0);
        if (null == quotaInstance || quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_STRING) {
            return null;
        } else if (quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_DOUBLE){
            result = quotaInstance.getFloatValue();
        }
        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}
