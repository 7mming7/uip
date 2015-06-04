package com.sq.comput.service;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.LimitInstance;
import com.sq.comput.domain.LimitTemplate;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.LimitInstanceRepository;
import com.sq.comput.repository.LimitTempRepository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/5/15
 * Time: 16:39
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class LimitInstanceService extends BaseService<LimitInstance,Integer> {

    private static Logger log = LoggerFactory.getLogger(LimitInstanceService.class);

    @Autowired
    @BaseComponent
    private LimitInstanceRepository limitInstanceRepository;

    @Autowired
    private LimitTempRepository limitTempRepository;

    @Autowired
    private IndicatorInstanceRepository indicatorInstanceRepository;


    /**
     * 计算指标限值，并生成记录
     * @param indicatorInstanceList
     */
    public void limitRealTimeCalculate(List<IndicatorInstance> indicatorInstanceList) {
        Map<String,IndicatorInstance> indicatorInstanceMap = new ConcurrentHashMap<String,IndicatorInstance>();
        List<Long> indicatorTempIdList = new LinkedList<Long>();
        for (IndicatorInstance indicatorInstance:indicatorInstanceList) {
            indicatorInstanceMap.put(indicatorInstance.getIndicatorCode(),indicatorInstance);
            indicatorTempIdList.add(indicatorInstance.getIndicatorTemp().getId());
        }
        if (indicatorTempIdList.isEmpty()) {
            return;
        }
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorTemp.id", MatchType.IN, indicatorTempIdList);
        List<LimitTemplate> limitTemplateList = limitTempRepository.findAll(searchable).getContent();

        List<LimitInstance> limitInstanceList = new ArrayList<LimitInstance>();
        for (LimitTemplate limitTemplate:limitTemplateList) {
            if (null != generateCalculateSingleLimitRecord(limitTemplate,indicatorInstanceMap))
            limitInstanceList.add(generateCalculateSingleLimitRecord(limitTemplate,indicatorInstanceMap));
        }

        limitInstanceRepository.save(limitInstanceList);
    }

    /**
     * 生成限值实时计算记录
     * @param limitTemplate         限值配置模板对象
     * @param indicatorInstanceMap  计算好的指标实例map
     * @return  计算单个指标限值
     */
    public LimitInstance generateCalculateSingleLimitRecord(LimitTemplate limitTemplate,
                                                            Map<String,IndicatorInstance> indicatorInstanceMap) {
        LimitInstance limitInstance = new LimitInstance(limitTemplate);
        String limitExpression = limitInstance.getExpression();
        String limitValue = "";
        switch (limitInstance.getExpType()) {
            case IndicatorConsts.EXPRESSION_NATIVE:
                limitValue = limitExpression;
                break;
            case IndicatorConsts.EXPRESSION_DYNAMIC:
                if (null == execLimitCal(limitExpression,indicatorInstanceMap)) return null;
                limitValue = execLimitCal(limitExpression,indicatorInstanceMap).toString();
                break;
        }
        limitInstance.setLimitValue(Double.parseDouble(limitValue));
        limitInstance.setCreateTime(Calendar.getInstance());

        IndicatorInstance indicatorInstance = indicatorInstanceMap.get(limitTemplate.getIndicatorTemp().getIndicatorCode());
        if (null == indicatorInstance) return null;

        limitInstance.setIndicatorInstance(indicatorInstance);
        if (limitTemplate.getLimitType() == IndicatorConsts.LIMIT_TYPE_UPPER) {
            limitInstance.setTransfinite(Double.parseDouble(limitValue)>indicatorInstance.getFloatValue());
        } else if (limitTemplate.getLimitType() == IndicatorConsts.LIMIT_TYPE_LOWER) {
            limitInstance.setTransfinite(Double.parseDouble(limitValue)<indicatorInstance.getFloatValue());
        }

        return limitInstance;
    }

    /**
     * 执行动态表达式的计算，根据指标实例map中指标的数据
     * @param dynamicExpression     动态带参表达式
     * @param indicatorInstanceMap  指标实例计算map
     * @return 表达式计算结果
     */
    public Double execLimitCal(String dynamicExpression,
                               Map<String,IndicatorInstance> indicatorInstanceMap) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        List<String> variableList = ComputHelper.getVariableList(dynamicExpression,evaluator);
        for (String variable:variableList) {
            IndicatorInstance indicatorInstance = indicatorInstanceMap.get(variable);
            if (null == indicatorInstance & null == indicatorInstance.getFloatValue()) {
                return null;
            }
            String replaceVariable = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;

            dynamicExpression = dynamicExpression.replace(replaceVariable,indicatorInstance.getFloatValue().toString());
        }

        Double result = null;
        try {
            result = Double.valueOf(evaluator.evaluate(dynamicExpression));
        } catch (EvaluationException e) {
            log.error("限值计算结果出现错误. calculateExp->" + dynamicExpression, e);
        }
        return result;
    }

    /**
     * 基于某个日期进行限值计算
     * @param limitTemplate  限值配置模板
     * @param computCal      计算日期
     * @return  限值计算记录
     */
    public LimitInstance execLimitComput(LimitTemplate limitTemplate, Calendar computCal) {
        LimitInstance limitInstance = new LimitInstance(limitTemplate);
        String dynamicExpression = limitInstance.getExpression();
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        List<String> variableList = ComputHelper.getVariableList(dynamicExpression,evaluator);
        for (String variable:variableList) {
            Searchable searchable = Searchable.newSearchable()
                    .addSearchFilter("indicatorCode", MatchType.EQ, variable)
                    .addSearchFilter("instanceTime", MatchType.EQ, computCal);
            List<IndicatorInstance> indicatorInstanceList = indicatorInstanceRepository.findAll(searchable).getContent();

            Map<String,IndicatorInstance> indicatorInstanceMap = new HashMap<String,IndicatorInstance>();
            for (IndicatorInstance indicatorInstance:indicatorInstanceList) {
                indicatorInstanceMap.put(indicatorInstance.getIndicatorCode(),indicatorInstance);
                limitInstance = generateCalculateSingleLimitRecord(limitTemplate, indicatorInstanceMap);
            }
        }
        limitInstanceRepository.saveAndFlush(limitInstance);
        return limitInstance;
    }
}