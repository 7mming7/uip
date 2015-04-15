package com.sq.jobschedule.service;

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
}
