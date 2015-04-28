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

    /**
     * 接口数据汇集到系统的最小维度小时级
     * @param computCal 计算时间
     */
    public void interfaceDataGather (Calendar computCal) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_INTERFACE);
        Page<IndicatorTemp> indicatorTempPage = this.indicatorTempRepository.findAll(searchable);
        List<IndicatorTemp> indicatorTempList = indicatorTempPage.getContent();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            log.info("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            sendCalculateComm(indicatorTemp, computCal, new InterfaceStrategy());
        }
    }

    /**
     * 计算指标数据计算
     * @param computCal 计算时间
     */
    public void calculateDataGater (Calendar computCal) {
        ConcurrentHashMap<String, Integer> threadCalculateMap = ComputHelper.threadCalculateMap;

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

        LinkedBlockingQueue<IndicatorTemp> indicatorSyncQueue = new LinkedBlockingQueue<IndicatorTemp>();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            log.info("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            indicatorSyncQueue.add(indicatorTemp);
        }
        stepCalculateIndicator(indicatorSyncQueue, computCal);
    }

    /**
     * 分步执行同步队列中的指标计算
     * @param indicatorSyncQueue
     * @param computCal
     */
    public synchronized void stepCalculateIndicator (LinkedBlockingQueue<IndicatorTemp> indicatorSyncQueue, Calendar computCal) {
        Long costTime = 0L;
        Long now = System.currentTimeMillis();
        while(!indicatorSyncQueue.isEmpty() && costTime <= ComputHelper.requestWaitTimeOutValue*10000L){
            IndicatorTemp head = indicatorSyncQueue.poll();
            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                log.error("线程停顿出错!", e);
            }
            log.error("indicatorSyncQueue size======" + indicatorSyncQueue.size());
            if (checkCalculateCondition(head)) {
                switch (head.getCalType()) {
                    case IndicatorConsts.CALTYPE_INVENTORY:
                        sendCalculateComm(head, computCal, new InventoryStrategy());
                        break;
                    case IndicatorConsts.CALTYPE_ORIGINAL:
                        sendCalculateComm(head, computCal, new PrimaryStrategy());
                        break;
                }
                ComputHelper.threadCalculateMap.put(head.getIndicatorCode(), IndicatorConsts.VALUE_TYPE_DOUBLE);
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
        boolean flag = true;
        for (String variable : variableList) {
            log.error("variable======" + variable);
            if (ComputHelper.threadCalculateMap.get(variable) == null) {
                log.error(indicatorTemplate.getIndicatorName() + "---" + indicatorTemplate.getIndicatorCode() +
                        " 指标的关联指标==" + variable + "不存在！");
            } else {
                flag = flag && ComputHelper.threadCalculateMap.get(variable) != null;
            }
            log.error("flag======" + flag);
        }
        return flag;
    }

    /**
     * 发送指标计算命令，将计算线程加到计算线程池中
     * @param indicatorTemplate  指标模板
     * @param computCal          计算时间
     */
    public synchronized void sendCalculateComm (IndicatorTemp indicatorTemplate,
                                                Calendar computCal, IComputStrategy iComputStrategy) {
        IndiComputThread indiComputThread = new IndiComputThread();
        indiComputThread.setComputCal(computCal);
        indiComputThread.setAssignMillions(System.currentTimeMillis());
        indiComputThread.setiComputStrategy(iComputStrategy);
        indiComputThread.setIndicatorTemp(indicatorTemplate);
        indiComputThread.run();
        /*ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();
        if (!_instance.isTerminated()) {
            _instance.execute(indiComputThread);
        }*/
    }

    /**
     * 重新计算指标集合关联的指标集合，自计算日期至当前日期
     * @param computCal 计算日期
     * @param indicatorTempList 需要重新计算的指标基
     * @return 重新计算是否成功
     */
    public void reComputIndicator (Calendar computCal, List<IndicatorTemp> indicatorTempList) {
        TreeMap<Integer,List<IndicatorTemp>> integerListTreeMap = new TreeMap<>();
        integerListTreeMap = buildIndiSortTreeMap(integerListTreeMap,indicatorTempList,0);
        List<Calendar> calendarList = DateUtil.dayListSinceCal(computCal);

        /** 删除需要重新计算的指标实例的数据 */
        deleteNeedReComputIndicator(computCal, indicatorTempList);

        Iterator iterator = integerListTreeMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry ent = (Map.Entry )iterator.next();
            List<IndicatorTemp> indicatorTemps = (List<IndicatorTemp>)ent.getValue();
            for (IndicatorTemp indicatorTemp:indicatorTemps){
                for (Calendar reComputCal:calendarList){
                    switch (indicatorTemp.getCalType()) {
                        case IndicatorConsts.CALTYPE_INVENTORY:
                            sendCalculateComm(indicatorTemp, reComputCal, new InventoryStrategy());
                            break;
                        case IndicatorConsts.CALTYPE_ORIGINAL:
                            sendCalculateComm(indicatorTemp, reComputCal, new PrimaryStrategy());
                            break;
                    }
                }
            }
        }
    }

    /**
     * 删除需要重新计算的指标实例的数据
     * @param computCal          指定时间
     * @param indicatorTempList  关联指标对象
     */
    public void deleteNeedReComputIndicator (Calendar computCal, List<IndicatorTemp> indicatorTempList) {
        Searchable searchable = Searchable.newSearchable();
        int startComputDateNum = Integer.parseInt(DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT));
        searchable.addSearchFilter("statDateNum",MatchType.GTE,startComputDateNum);
        List<String> indicatorCodeList = new ArrayList<String>();
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
    public TreeMap<Integer,List<IndicatorTemp>> buildIndiSortTreeMap (TreeMap<Integer,List<IndicatorTemp>> integerListTreeMap, List<IndicatorTemp> indicatorTempList, Integer level) {
        if (indicatorTempList.isEmpty())
            return integerListTreeMap;

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.NE, IndicatorConsts.DATASOURCE_INTERFACE);
        level = level + 1;
        OrCondition orCondition = new OrCondition();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            orCondition.add(Condition.newCondition("calculateExpression",MatchType.LIKE, "%" + indicatorTemp.getIndicatorCode() + "%"));
        }
        searchable.or(orCondition);
        List<IndicatorTemp> indicatorTemps = indicatorTempRepository.findAll(searchable).getContent();
        integerListTreeMap.put(level,indicatorTemps);

        return buildIndiSortTreeMap(integerListTreeMap,indicatorTemps,level);
    }
}
