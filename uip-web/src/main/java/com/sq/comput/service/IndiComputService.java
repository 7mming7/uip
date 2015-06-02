package com.sq.comput.service;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.comput.strategy.*;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.Condition;
import com.sq.entity.search.condition.OrCondition;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 指标计算业务类.
 * User: shuiqing
 * Date: 2015/4/17
 * Time: 11:02
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class IndiComputService extends BaseService<IndicatorInstance,Long>{

    private static Logger log = LoggerFactory.getLogger(IndiComputService.class);

    @Autowired
    @BaseComponent
    private IndicatorInstanceRepository indicatorInstanceRepository;

    @Autowired
    private IndicatorTempRepository indicatorTempRepository;

    @Autowired
    private LimitInstanceService limitInstanceService;

    private static LinkedBlockingQueue<IndicatorTemp> indicatorSyncQueue = new LinkedBlockingQueue<IndicatorTemp>();

    /**
     * 接口数据汇集到系统的最小维度小时级
     * @param computCal 计算时间
     */
    public void interfaceDataGather (Calendar computCal) {
        ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_INTERFACE);
        Page<IndicatorTemp> indicatorTempPage = this.indicatorTempRepository.findAll(searchable);
        List<IndicatorTemp> indicatorTempList = indicatorTempPage.getContent();

        List<IndicatorInstance> indicatorInstanceList = new LinkedList<IndicatorInstance>();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            log.info("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            IndicatorInstance indicatorInstance = sendCalculateComm(_instance, indicatorTemp, computCal, new InterfaceStrategy());
            if (null != indicatorInstance) {
                indicatorInstanceList.add(indicatorInstance);
            }
        }

        limitInstanceService.limitRealTimeCalculate(indicatorInstanceList);
    }

    /**
     * 计算指标数据计算
     * @param computCal 计算时间
     */
    public void calculateDataGater (Calendar computCal) {
        ComputHelper.threadCalculateMap = new ConcurrentHashMap<String, Integer>();
        ConcurrentHashMap<String, Integer> threadCalculateMap = ComputHelper.threadCalculateMap;

        ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_CALCULATE);
        Page<IndicatorTemp> indicatorTempPage = this.indicatorTempRepository.findAll(searchable);
        List<IndicatorTemp> indicatorTempList = indicatorTempPage.getContent();
        searchable.removeSearchFilter("dataSource", MatchType.EQ);

        int computInt = Integer.parseInt(DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT));
        searchable.addSearchFilter("statDateNum", MatchType.EQ, computInt);
        List<IndicatorInstance> indicatorInstanceList = indicatorInstanceRepository.findAll(searchable).getContent();
        for (IndicatorInstance indicatorInstance:indicatorInstanceList) {
            threadCalculateMap.put(indicatorInstance.getIndicatorCode(), indicatorInstance.getValueType());
        }

        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            log.info("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            indicatorSyncQueue.add(indicatorTemp);
        }
        stepCalculateIndicator(_instance, indicatorSyncQueue, computCal);
        limitAllDayComput(computCal);
    }

    /**
     * 分步执行同步队列中的指标计算
     * @param indicatorSyncQueue
     * @param computCal
     */
    public synchronized void stepCalculateIndicator (ThreadPoolExecutor _instance,
                                                     LinkedBlockingQueue<IndicatorTemp> indicatorSyncQueue,
                                                     Calendar computCal) {
        Long costTime = 0L;
        Long now = System.currentTimeMillis();
        while(!indicatorSyncQueue.isEmpty() && costTime <= ComputHelper.requestWaitTimeOutValue*1000L){
            IndicatorTemp head = indicatorSyncQueue.poll();
            try {
                Thread.sleep(10l);
            } catch (InterruptedException e) {
                log.error("线程停顿出错!", e);
            }
            log.error("indicatorSyncQueue size======" + indicatorSyncQueue.size());
            if (checkCalculateCondition(head)) {
                switch (head.getCalType()) {
                    case IndicatorConsts.CALTYPE_INVENTORY:
                        sendCalculateComm(_instance, head, computCal, new InventoryStrategy());
                        break;
                    case IndicatorConsts.CALTYPE_ORIGINAL:
                        sendCalculateComm(_instance, head, computCal, new PrimaryStrategy());
                        break;
                }
            } else {
                try {
                    indicatorSyncQueue.put(head);
                } catch (InterruptedException e) {
                    log.error("将指标模板压入队列出错!", e);
                }
            }
            costTime = System.currentTimeMillis() - now;
            log.error("costTime-------" + costTime);
        }

        while(!indicatorSyncQueue.isEmpty()) {
            IndicatorTemp indicatorTemp = (IndicatorTemp) indicatorSyncQueue.poll();
            log.error("indicatorName->" + indicatorTemp.getIndicatorName());
            log.error("indicatorCode->" + indicatorTemp.getIndicatorCode());
            log.error("calculateExpression->" + indicatorTemp.getCalculateExpression());
            log.error("----------------------------------------");
        }
        log.error("已完成线程数：" + _instance.getCompletedTaskCount());
        log.error("Task 总数：" + _instance.getTaskCount());
        log.error("map：" + ComputHelper.threadCalculateMap.size());
    }

    /**
     * 指标计算可行性检测
     * @param indicatorTemplate 待检测指标模板
     * @return 条件检测结果
     */
    public synchronized boolean checkCalculateCondition (IndicatorTemp indicatorTemplate) {
        Evaluator evaluator = ComputHelper.getEvaluatorInstance();
        log.error("indicatorTemplate=====" + indicatorTemplate.getIndicatorCode());
        List<String> variableList = ComputHelper.getVariableList(indicatorTemplate.getCalculateExpression(), evaluator);
        boolean result = true;
        for (String variable : variableList) {
            boolean flag = false;
            log.error(indicatorTemplate.getIndicatorCode() + "，variable======" + variable);
            if (ComputHelper.threadCalculateMap.get(variable) == null) {
                log.error(indicatorTemplate.getIndicatorName() + "---" + indicatorTemplate.getIndicatorCode() +
                        " 指标的关联指标==" + variable + "不存在！");
            } else {
                flag = ComputHelper.threadCalculateMap.get(variable) != null;
            }
            log.error(indicatorTemplate.getIndicatorCode() + ",variable->" + variable + "，flag======" + flag);
            result = result&&flag;
        }
        return result;
    }

    /**
     * 发送指标计算命令，将计算线程加到计算线程池中
     * @param indicatorTemplate  指标模板
     * @param computCal          计算时间
     */
    public synchronized IndicatorInstance sendCalculateComm (ThreadPoolExecutor _instance, IndicatorTemp indicatorTemplate,
                                                Calendar computCal, IComputStrategy iComputStrategy) {
        IndiComputTask indiComputThread = new IndiComputTask();
        indiComputThread.setComputCal(computCal);
        indiComputThread.setAssignMillions(System.currentTimeMillis());
        indiComputThread.setiComputStrategy(iComputStrategy);
        indiComputThread.setIndicatorTemp(indicatorTemplate);
        /*indiComputThread.start();*/
        Future<IndicatorInstance> future = _instance.submit(indiComputThread);
        IndicatorInstance indicatorInstance = null;
        try {
            while (!future.isDone()) {
                indicatorInstance = future.get();
            }
        } catch (Exception e) {
            log.error("IndiComputService->sendCalculateComm 线程执行出现异常.",e);
        }
        return indicatorInstance;
    }

    /**
     * 重新计算指标集合关联的指标集合，自计算日期至当前日期
     * @param computCal 计算日期
     * @param indicatorTempList 需要重新计算的指标基
     * @return 重新计算是否成功
     */
    public void reComputIndicator (Calendar computCal, List<IndicatorTemp> indicatorTempList) {
        TreeMap<Integer,Set<IndicatorTemp>> integerListTreeMap = new TreeMap<>();
        if (indicatorTempList.isEmpty()) {
            return;
        }
        integerListTreeMap = buildIndiSortTreeMap(integerListTreeMap,indicatorTempList,0);
        Calendar tempCal = (Calendar) computCal.clone();
        List<Calendar> calendarList = DateUtil.dayListSinceCal(tempCal);

        /** 删除需要重新计算的指标实例的数据 */
        deleteNeedReComputIndicator(computCal, integerListTreeMap);

        ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();
        Iterator iterator = integerListTreeMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry ent = (Map.Entry )iterator.next();
            Set<IndicatorTemp> indicatorTemps = (Set<IndicatorTemp>)ent.getValue();
            for (IndicatorTemp indicatorTemp:indicatorTemps){
                log.error(indicatorTemp.getId() + "->" + indicatorTemp.getIndicatorCode());
                for (Calendar reComputCal:calendarList){
                    deleteIndicatorTemp(reComputCal,indicatorTemp);
                    switch (indicatorTemp.getCalType()) {
                        case IndicatorConsts.CALTYPE_INVENTORY:
                            sendCalculateComm(_instance, indicatorTemp, reComputCal, new InventoryStrategy());
                            break;
                        case IndicatorConsts.CALTYPE_ORIGINAL:
                            sendCalculateComm(_instance, indicatorTemp, reComputCal, new PrimaryStrategy());
                            break;
                    }
                    log.error("**indicatorCode->" + indicatorTemp.getIndicatorCode() + ",reComputCal-》"
                            + DateUtil.formatCalendar(reComputCal,DateUtil.DATE_FORMAT_DAFAULT));
                    LimitComputTask limitComputTask = new LimitComputTask(indicatorTemp,reComputCal);
                    try {
                        limitComputTask.call();
                    } catch (Exception e) {
                        log.error("limitComputTask call()执行出现异常->" + indicatorTemp.getIndicatorCode(),e);
                    }
                }
            }
        }

        _instance.shutdown();
    }

    /**
     * 删除指标在一段时间的数据
     * @param computCal
     * @param indicatorTemp
     */
    public void deleteIndicatorTemp (Calendar computCal, IndicatorTemp indicatorTemp) {
        Searchable searchable = Searchable.newSearchable();
        int startComputDateNum = Integer.parseInt(DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT));
        searchable.addSearchFilter("statDateNum",MatchType.GTE,startComputDateNum)
                .addSearchFilter("indicatorCode", MatchType.EQ, indicatorTemp.getIndicatorCode());
        indicatorInstanceRepository.delete(indicatorInstanceRepository.findAll(searchable).getContent());
    }

    /**
     * 删除需要重新计算的指标实例的数据
     * @param computCal          指定时间
     * @param integerListTreeMap  关联指标对象
     */
    public void deleteNeedReComputIndicator (Calendar computCal, TreeMap<Integer,Set<IndicatorTemp>> integerListTreeMap) {
        Searchable searchable = Searchable.newSearchable();
        int startComputDateNum = Integer.parseInt(DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT));
        searchable.addSearchFilter("statDateNum",MatchType.GTE,startComputDateNum);
        List<String> indicatorCodeList = new ArrayList<String>();
        List<IndicatorTemp> indicatorTempList = new ArrayList<IndicatorTemp>();
        for (Map.Entry<Integer, Set<IndicatorTemp>> entry : integerListTreeMap.entrySet()) {
            indicatorTempList.addAll((Set<IndicatorTemp>)entry.getValue());
        }
        for (IndicatorTemp indicatorTemp:indicatorTempList){
            indicatorCodeList.add(indicatorTemp.getIndicatorCode());
        }
        searchable.addSearchFilter("indicatorCode",MatchType.IN, indicatorCodeList);
        indicatorInstanceRepository.delete(indicatorInstanceRepository.findAll(searchable).getContent());
    }

    /**
     * 根据指定的指标模板列表，构建指标排序二叉树
     * @param integerListTreeMap  指标排序二叉树
     * @param indicatorTempList   给定的指标模板列表
     * @param level  层级
     * @return
     */
    public TreeMap<Integer, Set<IndicatorTemp>> buildIndiSortTreeMap(
            TreeMap<Integer, Set<IndicatorTemp>> integerListTreeMap,
            List<IndicatorTemp> indicatorTempList,
            Integer level) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_CALCULATE);

        OrCondition orCondition = new OrCondition();
        for (IndicatorTemp indicatorTemp : indicatorTempList) {
            orCondition.add(Condition.newCondition("calculateExpression", MatchType.LIKE, "%#{" + indicatorTemp.getIndicatorCode() + "}%"));
            /*orCondition.add(Condition.newCondition("indicatorCode", MatchType.NE, indicatorTemp.getIndicatorCode()));*/
        }
        searchable.or(orCondition);
        List<IndicatorTemp> indicatorTemps = indicatorTempRepository.findAll(searchable).getContent();

        if (indicatorTemps.isEmpty()) {
            return integerListTreeMap;
        }

        Set<IndicatorTemp> indicatorTempSet = new HashSet<>(indicatorTemps);

        integerListTreeMap.put(level, indicatorTempSet);
        level = level + 1;
        return buildIndiSortTreeMap(integerListTreeMap, indicatorTemps, level);
    }

    /**
     * 计算给定日期一日的所有的限值
     * @param computCal
     */
    public void limitAllDayComput (Calendar computCal) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("statDateNum", MatchType.EQ,
                        DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT))
                .addSearchFilter("fetchCycle", MatchType.NE, IndicatorConsts.FETCH_CYCLE_HOUR);
        List<IndicatorInstance> indicatorInstanceList = indicatorInstanceRepository.findAll(searchable).getContent();
        limitInstanceService.limitRealTimeCalculate(indicatorInstanceList);
    }
}
