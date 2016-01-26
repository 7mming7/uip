package com.sq.quota.function.logical;

import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.util.DateUtil;
import com.sq.util.NumberUtils;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private static OriginalDataRepository originalDataRepository = SpringUtils.getBean(OriginalDataRepository.class);

    @Override
    public String getName() {
        return "inst";
    }

    @Override
    public FunctionResult execute(Evaluator evaluator, String arguments) throws FunctionException {
        Double result = null;
        ArrayList<String> argList = FunctionHelper.getStrings(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);
        String quotaCode = (String)argList.get(0);
        quotaCode = quotaCode.substring(1, quotaCode.length() - 1);
        log.error("quotaCode:" + quotaCode);

        String computDateInt = (String)argList.get(1);
        log.error("computDateInt:" + computDateInt);
        computDateInt = computDateInt.substring(1, computDateInt.length() - 1);

        Calendar computCal = null;
        try {
            computCal = DateUtil.stringToCalendar(computDateInt, DateUtil.DATE_FORMAT_YMDHM);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<OriginalData> frontOriginalDataList = originalDataRepository.fetchFrontOriginalDataByCal(quotaCode,computCal);
        List<OriginalData> behindOriginalDataList = originalDataRepository.fetchBehindOriginalDataByCal(quotaCode, computCal);
        if (frontOriginalDataList.isEmpty() && behindOriginalDataList.isEmpty()) {
            return new FunctionResult(null,
                    FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
        } else if (!frontOriginalDataList.isEmpty() && behindOriginalDataList.isEmpty()) {
            result = Double.parseDouble(frontOriginalDataList.get(0).getItemValue());
        } else if (frontOriginalDataList.isEmpty() && !behindOriginalDataList.isEmpty()) {
            result = Double.parseDouble(behindOriginalDataList.get(0).getItemValue());
        } else {
            Double frontValue = null;
            Double behindValue = null;
            OriginalData frontOd = frontOriginalDataList.get(0);
            OriginalData behindOd = behindOriginalDataList.get(0);
            log.error(frontOd.toString());
            log.error(behindOd.toString());
            if (NumberUtils.isNumeric(frontOd.getItemValue().trim())) {
                frontValue = Double.parseDouble(frontOd.getItemValue().trim());
            }
            if (NumberUtils.isNumeric(behindOd.getItemValue().trim())) {
                behindValue = Double.parseDouble(behindOd.getItemValue().trim());
            }
            Calendar firstCal = frontOd.getInstanceTime();
            Calendar lastCal = behindOd.getInstanceTime();
            long betMinutesTwoInput = DateUtil.getMinutesBetTwoCal(firstCal, lastCal);
            long betMinutesComputCal = DateUtil.getMinutesBetTwoCal(firstCal, computCal);
            log.error("betMinutesTwoInput--------- " + betMinutesTwoInput);
            log.error("betMinutesComputCal-------- " + betMinutesComputCal);
            log.error("frontValue------- " + frontValue);
            log.error("behindValue-------- " + behindValue);

            if (null == frontValue || null == behindValue) {
                return new FunctionResult(null,
                        FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
            }

            if (frontValue.equals(behindValue)) {
                result = frontValue;
            } else {
                result = frontValue + betMinutesComputCal*((behindValue - frontValue)/betMinutesTwoInput);
            }
        }

        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
    }
}
