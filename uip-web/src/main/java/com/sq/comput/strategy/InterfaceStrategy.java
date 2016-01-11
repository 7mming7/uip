package com.sq.comput.strategy;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.SearchFilter;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.quota.component.QuotaBaseConfigure;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class InterfaceStrategy extends IComputStrategy {

    private Logger log = LoggerFactory.getLogger(InterfaceStrategy.class);

    private static OriginalDataRepository originalDataRepository = SpringUtils.getBean(OriginalDataRepository.class);

    private static MesuringPointRepository mesuringPointRepository = SpringUtils.getBean(MesuringPointRepository.class);

    @Override
    public Object execIndiComput(IndicatorTemp indicatorTemp, Calendar computCal) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        String calculateExp = indicatorTemp.getCalculateExpression();
        List<String> variableList = ComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty()) {
            log.error("指标：" + indicatorTemp.getIndicatorCode() + "-的表达式没有动态参数!");
            return null;
        }
        String checkPoint = variableList.get(0);

        MesuringPoint mesuringPoint = null;
        List<MesuringPoint> mesuringPointList = mesuringPointRepository.findAll(
                Searchable.newSearchable().addSearchFilter("targetCode",MatchType.EQ,checkPoint)).getContent();
        if (mesuringPointList.isEmpty()) {
            log.error("mesuringPoint中不存在" + checkPoint + "相关的编码!");
            return null;
        }
        mesuringPoint = mesuringPointList.get(0);

        String sourceYMDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_MONTH);
        String sourceYMDDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT);
        String currentDate = DateUtil.formatCalendar(Calendar.getInstance(), DateUtil.DATE_FORMAT_DAFAULT);
        String tableName = "t_originaldata";
        if (!sourceYMDDate.equals(currentDate)) {
            tableName = tableName + "_migration" + sourceYMDate;
        }
        System.out.println("-----------" + DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_YMDHMS));
        /** 计算该接口指标的数据汇聚维度 */
        Integer preMinutes = calPreMinutes(indicatorTemp);
        List<OriginalData> originalDataList = originalDataRepository.listAnHourPreOriginalData(tableName, mesuringPoint.getTargetCode(),preMinutes, computCal);

        StringBuilder variableBuilder = new StringBuilder();
        if (originalDataList.isEmpty()) {
            log.error("指标" + indicatorTemp.getIndicatorCode() + "关联测点->" + checkPoint + " 没有数据!");
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
            log.error("**计算指标: " + indicatorTemp.getIndicatorCode(), e);
        }
        Double calResult = Double.parseDouble(result);
        return calResult;
    }
}
