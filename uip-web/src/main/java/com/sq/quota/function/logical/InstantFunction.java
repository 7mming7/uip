package com.sq.quota.function.logical;

import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 瞬时值函数
 * User: shuiqing
 * Date: 15/11/11
 * Time: 下午3:24
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class InstantFunction implements Function {

    private static final Logger log = LoggerFactory.getLogger(DateTimeFunction.class);

    private OriginalDataRepository originalDataRepository = SpringUtils.getBean(OriginalDataRepository.class);

    @Override
    public String getName() {
        return "inst";
    }

    @Override
    public FunctionResult execute(Evaluator evaluator, String arguments) throws FunctionException {
        Double result = null;
        ArrayList<Object> argList = FunctionHelper.getOneStringAndOneInteger(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);
        String quotaCode = (String)argList.get(0);
        Integer computDateInt = (Integer)argList.get(1);
        Calendar computCal = DateUtil.intDate2Calendar(computDateInt);

        OriginalData frontOriginalData = originalDataRepository.fetchFrontOriginalDataByCal(quotaCode,computCal);
        OriginalData behindOriginalData = originalDataRepository.fetchBehindOriginalDataByCal(quotaCode, computCal);
        if (frontOriginalData == null && behindOriginalData == null) {
            return null;
        } else if (frontOriginalData == null && behindOriginalData != null) {
            result = Double.parseDouble(behindOriginalData.getItemValue());
        } else if (frontOriginalData != null && behindOriginalData == null) {
            result = Double.parseDouble(frontOriginalData.getItemValue());
        } else {
            Double frontValue = Double.parseDouble(frontOriginalData.getItemValue());
            Double behindValue = Double.parseDouble(behindOriginalData.getItemValue());
            int betMinutesTwoInput = DateUtil.getMinutesBetTwoCal(frontOriginalData.getInstanceTime(), behindOriginalData.getInstanceTime());
            int betMinutesComputCal = DateUtil.getMinutesBetTwoCal(frontOriginalData.getInstanceTime(), computCal);
            result = frontValue + betMinutesComputCal*(betMinutesTwoInput/(behindValue - frontValue));
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
    }
}
