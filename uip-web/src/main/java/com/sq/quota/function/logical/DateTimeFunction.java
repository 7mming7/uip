package com.sq.quota.function.logical;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaResetRecord;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.quota.repository.QuotaResetRecordRepository;
import com.sq.quota.service.QuotaComputInsService;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sq.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(DateTimeFunction.class);

    private QuotaInstanceRepository quotaInstanceRepository = SpringUtils.getBean(QuotaInstanceRepository.class);

    private QuotaResetRecordRepository quotaResetRecordRepository = SpringUtils.getBean(QuotaResetRecordRepository.class);

    public String getName() {
        return "dateTime";
    }

    @Override
    public FunctionResult execute(Evaluator evaluator, String arguments)
            throws FunctionException {
        Double result = null;
        ArrayList<String> argList = FunctionHelper.getStrings(arguments,
                EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);
        log.error("*********************DateTimeFunction**********************");

        String cycleStr = (String)argList.get(0).trim();
        cycleStr = cycleStr.substring(1,cycleStr.length()-1);

        Integer disDateInt = Double.valueOf(argList.get(1)).intValue();

        String assQuotaCode = (String)argList.get(2).trim();
        assQuotaCode = assQuotaCode.substring(1,assQuotaCode.length()-1);

        BigDecimal bigDecimal = new BigDecimal((String)argList.get(3).trim());
        String computCal = bigDecimal.toPlainString();

        //计算指标的迁移之后的日期
        String assComputCal = DateUtil.dateMigrate(cycleStr, disDateInt, computCal);

        log.error("CycleStr:" + cycleStr
                + ",disDateInt: " + disDateInt
                + ",assQuotaCode: " + assQuotaCode
                + ",Start dateMigrate computCal: " + computCal
                + ",End dateMigrate computCal: " + assComputCal);

        //检查指标是否需要重置
        result = checkQuotaReset(assQuotaCode,assComputCal,computCal);
        if (null != result) {
            return new FunctionResult(result.toString(),
                    FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
        }

        List<QuotaInstance> quotaInstanceList = quotaInstanceRepository.listQuotaInstanceInstTime(assQuotaCode,assComputCal);
        if (quotaInstanceList.isEmpty())
            return new FunctionResult(null,
                FunctionConstants.FUNCTION_RESULT_TYPE_STRING);

        QuotaInstance quotaInstance = quotaInstanceList.get(0);
        if (null == quotaInstance || quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_STRING) {
            return new FunctionResult(null,
                    FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
        } else if (quotaInstance.getValueType() == QuotaConsts.VALUE_TYPE_DOUBLE){
            result = quotaInstance.getFloatValue();
        }
        log.error("assQuotaCode->" + assQuotaCode + ",dateMigrate->" + assComputCal + ",result->" + result);
        return new FunctionResult(result.toString(),
                FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
    }

    /**
     * 检查指标重置记录
     * @param assQuotaCode  关联的指标编码
     * @param assComputCal
     * @return
     */
    public Double checkQuotaReset(String assQuotaCode, String assComputCal, String currComputCal){
        List<QuotaResetRecord> quotaResetRecordList = quotaResetRecordRepository.fetchResetRecord(assQuotaCode,currComputCal);
        if (quotaResetRecordList.isEmpty()) return null;

        QuotaResetRecord quotaResetRecord = quotaResetRecordList.get(0);
        if(null == quotaResetRecord) {
            return null;
        }
        Calendar resetCal = quotaResetRecord.getResetDate();
        Calendar assCal = null;
        try {
            assCal = DateUtil.stringToCalendar(assComputCal,DateUtil.DATE_FORMAT_DAFAULT);
        } catch (ParseException e) {
            log.error("assQuotaCode:" + assQuotaCode + ",assComputCal:" + assComputCal + "stringToCalendar error!");
        }
        resetCal.add(Calendar.DAY_OF_MONTH, -1);
        if (DateUtils.isSameDay(resetCal, assCal)) {
            return quotaResetRecord.getResetValue();
        }
        return null;
    }
}
