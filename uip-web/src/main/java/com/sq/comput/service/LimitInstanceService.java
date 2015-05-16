package com.sq.comput.service;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.LimitInstance;
import com.sq.comput.domain.LimitTemplate;
import com.sq.comput.repository.LimitInstanceRepository;
import com.sq.comput.repository.LimitTempRepository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public class LimitInstanceService extends BaseService<LimitInstance,Integer> {

    private static Logger log = LoggerFactory.getLogger(LimitInstanceService.class);

    @Autowired
    @BaseComponent
    private LimitInstanceRepository limitInstanceRepository;

    @Autowired
    private LimitTempRepository limitTempRepository;

    /**
     * 计算指标限值，并生成记录
     * @param indicatorInstanceList
     */
    public void limitRealTimeCalculate(List<IndicatorInstance> indicatorInstanceList) {
        Map<String,IndicatorInstance> indicatorInstanceMap = new ConcurrentHashMap<String,IndicatorInstance>();
        List<Long> indicatorTempIdList = new LinkedList<Long>();
        for (IndicatorInstance indicatorInstance:indicatorInstanceList) {
            indicatorInstanceMap.put(indicatorInstance.getIndicatorCode(),indicatorInstance);
            indicatorTempIdList.add(indicatorInstance.getIndicatorTempId());
        }
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorTemp.id", MatchType.IN, indicatorTempIdList);
        List<LimitTemplate> limitTemplateList = limitTempRepository.findAll(searchable).getContent();

        List<LimitInstance> limitInstanceList = new ArrayList<LimitInstance>();
        for (LimitTemplate limitTemplate:limitTemplateList) {
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
                limitValue = execLimitCal(limitExpression,indicatorInstanceMap).toString();
        }
        limitInstance.setLimitValue(Double.parseDouble(limitValue));
        limitInstance.setCreateTime(Calendar.getInstance());

        IndicatorInstance indicatorInstance = indicatorInstanceMap.get(limitTemplate.getIndicatorTemp().getIndicatorCode());
        if (null != indicatorInstance) {
            limitInstance.setIndicatorInstance(indicatorInstance);
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
            dynamicExpression.replace(variable,indicatorInstance.getFloatValue().toString());
        }

        Double result = null;
        try {
            result = Double.valueOf(evaluator.evaluate(dynamicExpression));
        } catch (EvaluationException e) {
            log.error("限值计算结果出现错误. calculateExp->" + dynamicExpression, e);
        }
        return result;
    }
}