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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Override
    public FunctionResult execute(Evaluator evaluator, String arguments)
            throws FunctionException {
        Double result = null;
        ArrayList<String> argList = FunctionHelper.getStrings(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);
        System.out.println("*******************************************");

        String cycleStr = (String)argList.get(0);
        cycleStr = cycleStr.substring(1,cycleStr.length()-1);
        System.out.println("cycleStr: " + cycleStr);

        Integer disDateInt = Double.valueOf(argList.get(1)).intValue();
        System.out.println("disDateInt: " + disDateInt);

        String assQuotaCode = (String)argList.get(2);
        assQuotaCode = assQuotaCode.substring(1,assQuotaCode.length()-1);
        System.out.println("assQuotaCode: " + assQuotaCode);

        BigDecimal bigDecimal = new BigDecimal((String)argList.get(3));

        String computCal = bigDecimal.toPlainString();
        System.out.println("Start dateMigrate computCal: " + computCal);

        //计算指标的迁移之后的日期
        String assComputCal = DateUtil.dateMigrate(cycleStr, disDateInt, computCal);

        System.out.println("End dateMigrate computCal: " + assComputCal);
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorCode", MatchType.EQ, assQuotaCode)
                .addSearchFilter("statDateNum", MatchType.EQ, Integer.parseInt(assComputCal));


        List<QuotaInstance> quotaInstanceList = quotaInstanceRepository.findAll(searchable).getContent();
        if (quotaInstanceList.isEmpty())
            return new FunctionResult(null,
                FunctionConstants.FUNCTION_RESULT_TYPE_STRING);;

        QuotaInstance quotaInstance = quotaInstanceList.get(0);
        if (null == quotaInstance || quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_STRING) {
            result = null;
        } else if (quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_DOUBLE){
            result = quotaInstance.getFloatValue();
        }
        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
    }
}
