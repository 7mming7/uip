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
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    }

    /**
     * 分步执行同步队列中的指标计算
     * @param indicatorSyncQueue
     * @param computCal
     */
    public synchronized void stepCalculateIndicator (LinkedBlockingQueue<IndicatorTemp> indicatorSyncQueue, Calendar computCal) {
        Long costTime = 0L;
        Long now = System.currentTimeMillis();
        while(!indicatorSyncQueue.isEmpty() && costTime <= ComputHelper.requestWaitTimeOutValue*1000L){
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

        ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();
        if (!_instance.isTerminated()) {
            _instance.execute(indiComputThread);
        }
    }
}
