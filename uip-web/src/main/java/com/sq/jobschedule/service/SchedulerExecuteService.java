package com.sq.jobschedule.service;

import com.sq.comput.service.IndiComputService;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.jodbc.service.TradeService;
import com.sq.protocol.opc.component.BaseConfiguration;
import com.sq.protocol.opc.service.MesuringPointService;
import com.sq.protocol.opc.service.OriginalDataService;
import com.sq.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * 定时任务执行业务类.
 * User: shuiqing
 * Date: 2015/4/7
 * Time: 10:26
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class SchedulerExecuteService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerExecuteService.class);

    @Autowired
    private MesuringPointService mesuringPointService;

    @Autowired
    private OriginalDataService originalDataService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private IndiComputService indiComputService;

    /**
     * opc实时数据同步任务
     */
    public void syncOpcItem () {
        log.error("----------- Opc实时数据同步任务开始 -----------");
        for (int cid=1;cid<= BaseConfiguration.CONFIG_CLIENT_MAX;cid++) {
            mesuringPointService.fetchReadSyncItems(cid);
        }
        log.error("----------- Opc实时数据同步任务结束 -----------");
    }

    /**
     * 原始测点数据迁移任务
     */
    public void execDcsDataMigration () {
        log.error("----------- Opc原始数据迁移任务开始 -----------");
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.DAY_OF_MONTH, -1);
        originalDataService.opcDataMigration(DateUtil.formatCalendar(curr));
        log.error("----------- Opc原始数据迁移任务结束 -----------");
    }

    /**
     * 接口数据汇集任务
     */
    public void execInterfaceDataGather () {
        log.error("----------- 接口数据汇集任务开始 -----------");
        Calendar curr = Calendar.getInstance();
        indiComputService.interfaceDataGather(curr);
        log.error("----------- 接口数据汇集任务结束 -----------");
    }

    /**
     * 调用存储过程执行大屏数据更新
     */
    public void executeNjmbDataSync(){
        log.error("----------- 大屏数据同步计算任务开始 -----------");
        originalDataService.njmbDataSync();
        log.error("----------- 大屏数据同步计算任务结束 -----------");
    }

    /**
     * 地磅原始数据汇集
     */
    public void execLoadometerOrignalDataGathering () {
        log.error("----------- 地磅原始数据汇集任务开始 -----------");
        tradeService.listTradesBySearchable();
        log.error("----------- 地磅原始数据汇集任务结束 -----------");
    }

    /**
     * 地磅数据汇集
     */
    public void execLoadometerIndicatorData () {
        log.error("----------- 地磅指标数据汇集任务开始 -----------");
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.DAY_OF_MONTH, -1);
        tradeService.genTradeIndicators(curr);
        log.error("----------- 地磅指标数据汇集任务结束 -----------");
    }
}
