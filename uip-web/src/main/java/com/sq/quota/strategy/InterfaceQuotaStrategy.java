package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaTemp;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * 接口指标计算策略.
 * User: shuiqing
 * Date: 2015/4/21
 * Time: 11:53
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io
 * _
 * |_)._ _
 * | o| (_
 */
public class InterfaceQuotaStrategy extends IQuotaComputStrategy {

    private Logger log = LoggerFactory.getLogger(InterfaceQuotaStrategy.class);

    private static OriginalDataRepository originalDataRepository = SpringUtils.getBean(OriginalDataRepository.class);

    private static MesuringPointRepository mesuringPointRepository = SpringUtils.getBean(MesuringPointRepository.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = QuotaComputHelper.getEvaluatorInstance();
        String calculateExp = quotaTemp.getCalculateExpression();

        if (!checkQuotaExpression(evaluator,quotaTemp)) {
            return null;
        }
        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);
        String checkPoint = variableList.get(0);

        List<MesuringPoint> mesuringPointList = mesuringPointRepository.findAll(
                Searchable.newSearchable().addSearchFilter("targetCode",MatchType.EQ,checkPoint)).getContent();
        if (mesuringPointList.isEmpty()) {
            log.error("mesuringPoint中不存在" + checkPoint + "相关的编码!");
            return null;
        }
        MesuringPoint mesuringPoint = mesuringPointList.get(0);

        String sourceYMDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_MONTH);
        String sourceYMDDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT);
        String currentDate = DateUtil.formatCalendar(Calendar.getInstance(), DateUtil.DATE_FORMAT_DAFAULT);
        String tableName = "t_originaldata";
        if (!sourceYMDDate.equals(currentDate)) {
            tableName = tableName + "_migration" + sourceYMDate;
        }
        List<OriginalData> originalDataList = originalDataRepository.listAnHourPreOriginalData(tableName, mesuringPoint.getTargetCode(), computCal);

        StringBuilder variableBuilder = new StringBuilder();
        if (originalDataList.isEmpty()) {
            log.error("指标" + quotaTemp.getIndicatorCode() + "关联测点->" + checkPoint + " 没有数据!");
            return null;
        }
        for (OriginalData originalData : originalDataList) {
            String itemValue = originalData.getItemValue();
            variableBuilder.append(itemValue).append(",");
        }
        variableBuilder.deleteCharAt(variableBuilder.lastIndexOf(","));
        /** 替换动态参数变量 */
        String replaceVariable = EvaluationConstants.OPEN_VARIABLE + checkPoint + EvaluationConstants.CLOSED_BRACE;

        calculateExp = calculateExp.replace(replaceVariable, variableBuilder.toString());
        evaluator.putVariable(checkPoint, variableBuilder.toString());
        String result = "";
        try {
            result = evaluator.evaluate(calculateExp);
        } catch (EvaluationException e) {
            log.error("**计算指标: " + quotaTemp.getIndicatorCode(), e);
        }
        Double calResult = Double.parseDouble(result);
        return calResult;
    }
}
