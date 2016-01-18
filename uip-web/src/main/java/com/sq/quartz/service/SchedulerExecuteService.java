package com.sq.quartz.service;

import com.sq.loadometer.service.TradeDataService;
import com.sq.protocol.opc.component.BaseConfiguration;
import com.sq.protocol.opc.service.MesuringPointService;
import com.sq.protocol.opc.service.OriginalDataService;
import com.sq.protocol.opc.service.PushDataForYZTDService;
import com.sq.protocol.opc.service.PushDataThirdService;
import com.sq.quota.service.QuotaComputInsService;
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
public class SchedulerExecuteService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerExecuteService.class);

    @Autowired
    private MesuringPointService mesuringPointService;

    @Autowired
    private OriginalDataService originalDataService;

    @Autowired
    private QuotaComputInsService quotaComputInsService;

    @Autowired
    private PushDataThirdService pushDataThirdService;

    @Autowired
    private PushDataForYZTDService pushDataForYZTDService;

    @Autowired
    private TradeDataService tradeDataService;

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
        log.error("----------- 接口小时数据汇集任务开始 -----------");
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.MINUTE,-30);
        quotaComputInsService.regularDataGather(curr);
        log.error("----------- 接口小时数据汇集任务结束 -----------");
    }

    /**
     * 对昨日的数据进行重计算
     */
    public void execInterfaceBackComput (){
        log.error("----------- 接口数据昨日数据重计算开始 -----------");
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.DAY_OF_YEAR,-1);
        quotaComputInsService.interfaceIndicatorDataGater(curr);
        log.error("----------- 接口数据昨日数据重计算开始 -----------");
    }

    /**
     * 接口日数据汇集任务
     */
    /*public void execInterfaceIndicatorDataGater () {
        log.error("----------- 接口日指标数据汇集任务开始 -----------");
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.DAY_OF_MONTH, -1);
        quotaComputInsService.interfaceIndicatorDataGater(curr);
        log.error("----------- 接口日指标数据汇集任务结束 -----------");
    }*/

    /**
     * 调用存储过程执行大屏数据更新(南京光大)
     */
    public void executeNjmbDataSync(){
        log.error("----------- （南京光大）大屏数据同步计算任务开始 -----------");
        pushDataThirdService.updateScreenDisplay();
        log.error("----------- （南京光大）大屏数据同步计算任务结束 -----------");
    }

    /**
     * 调用存储过程执行大屏数据更新（扬州泰达）
     */
    public void executeYztdDataSync(){
        log.error("----------- （扬州泰达）大屏数据同步计算任务开始 -----------");
        pushDataForYZTDService.screenDataPush();
        log.error("----------- （扬州泰达）大屏数据同步计算任务结束 -----------");
    }

    /**
     * 地磅原始数据汇集
     */
    public void execLoadometerOrignalDataGathering () {
        log.error("----------- 地磅原始数据汇集任务开始 -----------");
        Calendar syncCal = Calendar.getInstance();
        syncCal.add(Calendar.HOUR_OF_DAY, -1);
        String syncDate = DateUtil.formatCalendar(syncCal,DateUtil.DATE_FORMAT_DAFAULT);
        tradeDataService.syncLoadometerTrade(syncDate);
        log.error("----------- 地磅原始数据汇集任务结束 -----------");
    }
}
