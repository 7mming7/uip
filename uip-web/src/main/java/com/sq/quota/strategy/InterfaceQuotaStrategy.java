package com.sq.quota.strategy;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.function.logical.LogicalFunctions;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionResult;
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

    private static LogicalFunctions logicalFunctions = SpringUtils.getBean(LogicalFunctions.class);

    @Override
    public Object execIndiComput(QuotaTemp quotaTemp, Calendar computCal) {
        Evaluator evaluator = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(evaluator);
        String calculateExp = quotaTemp.getCalculateExpression();

        if (!checkQuotaExpression(evaluator,quotaTemp)) {
            return null;
        }

        List<String> variableList = QuotaComputHelper.getVariableList(calculateExp,evaluator);
        if (variableList.isEmpty() || variableList.size() == 0)
            return null;
        String checkPoint = variableList.get(0);

        String result = null;
        List<Function> functionList = logicalFunctions.getFunctions();
        for(Function function:functionList) {
            if (calculateExp.contains(function.getName())) {
                String computExp = function.getName() + "('" + checkPoint + "','"
                        + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_YMDHM)
                        + "')";
                try {
                    log.error("execIndiComput  computExp --  " + computExp);
                    log.error("evaluator ---- " + (null == evaluator));
                    result = evaluator.evaluate(computExp);
                } catch (EvaluationException e) {
                    log.error("parseExpressionFront -> logicalFunctions 指标计算出现错误.calculateExp: " + computExp, e);
                } catch (Exception e) {
                    log.error("computExp ------" + computExp  + ", evaluator ----- " + (evaluator == null), e);
                }
                log.error("result ---- " + result);
                if (result.equals("'null'")) return null;
                Double calResult = Double.parseDouble(result);
                return calResult;
            }
        }

        /** 查找接口指标的关联测点 */
        List<MesuringPoint> mesuringPointList = mesuringPointRepository.findAll(
                Searchable.newSearchable().addSearchFilter("targetCode",MatchType.EQ,checkPoint)).getContent();
        if (mesuringPointList.isEmpty()) {
            log.error("mesuringPoint中不存在" + checkPoint + "相关的编码!");
            return null;
        }
        MesuringPoint mesuringPoint = mesuringPointList.get(0);

        /** 根据计算日期确定数据表 */
        String tableName = assignTableName(computCal);

        /** 计算该接口指标的数据汇聚维度 */
        Integer preMinutes = calPreMinutes(quotaTemp);

        /** 获取该测点原始数据集合 */
        List<OriginalData> originalDataList =
                originalDataRepository.listAnHourPreOriginalData(
                        tableName,
                        mesuringPoint.getTargetCode(),
                        preMinutes,
                        computCal);
        computCal.add(Calendar.MINUTE, 0 - preMinutes);

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

        try {
            result = evaluator.evaluate(calculateExp);
        } catch (EvaluationException e) {
            log.error("**计算指标: " + quotaTemp.getIndicatorCode(), e);
        }
        Double calResult = Double.parseDouble(result);
        return calResult;
    }

    /**
     * 指定表名
     *    参数时间与当前时间在同一日 tableName = t_originaldata
     *    否则 选取历史表
     * @param computCal 计算时间
     * @return 表名
     */
    public String assignTableName (Calendar computCal) {
        String sourceYMDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_MONTH);
        String sourceYMDDate = DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT);
        String currentDate = DateUtil.formatCalendar(Calendar.getInstance(), DateUtil.DATE_FORMAT_DAFAULT);
        String tableName = "t_originaldata";
        if (!sourceYMDDate.equals(currentDate)) {
            tableName = tableName + "_migration" + sourceYMDate;
        }
        return tableName;
    }
}
