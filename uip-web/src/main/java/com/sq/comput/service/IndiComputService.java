package com.sq.comput.service;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.comput.strategy.IComputStrategy;
import com.sq.comput.strategy.IndiComputThread;
import com.sq.comput.strategy.InterfaceStrategy;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     */
    public void interfaceDataGather (Calendar computCal) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_INTERFACE);
        Page<IndicatorTemp> indicatorTempPage = this.indicatorTempRepository.findAll(searchable);
        List<IndicatorTemp> indicatorTempList = indicatorTempPage.getContent();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            log.info("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            System.out.println("indicatorTemp:->" + indicatorTemp.getIndicatorName());
            sendCalculateComm(indicatorTemp, computCal, new InterfaceStrategy());
        }
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
