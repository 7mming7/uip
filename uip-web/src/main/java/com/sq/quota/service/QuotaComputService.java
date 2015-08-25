package com.sq.quota.service;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.quota.repository.QuotaTempRepository;
import com.sq.quota.strategy.IQuotaComputStrategy;
import com.sq.quota.strategy.InterfaceQuotaStrategy;
import com.sq.quota.strategy.QuotaComputTask;
import com.sq.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 指标运算服务类.
 * User: shuiqing
 * Date: 2015/8/25
 * Time: 9:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class QuotaComputService extends BaseService<QuotaInstance,Long> {

    private static final Logger log = LoggerFactory.getLogger(QuotaComputService.class);

    @Autowired
    @BaseComponent
    private QuotaInstanceRepository quotaInstanceRepository;

    @Autowired
    private QuotaTempRepository quotaTempRepository;

    /**
     * 接口数据汇集到系统的最小维度小时级
     * @param computCal 计算时间
     */
    public void interfaceDataGather (Calendar computCal) {
        ThreadPoolExecutor _instance = ComputHelper.initThreadPooSingleInstance();

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter(" DataSource", MatchType.EQ, IndicatorConsts.DATASOURCE_INTERFACE);
        Page<QuotaTemp> quotaTempPage = this.quotaTempRepository.findAll(searchable);
        List<QuotaTemp> quotaTempList = quotaTempPage.getContent();

        for (QuotaTemp quotaTemp:quotaTempList) {
            log.error(" QuotaTemp:->" + quotaTemp.getIndicatorName());
            sendCalculateComm(quotaTemp, computCal, new InterfaceQuotaStrategy());
        }
    }

    /**
     * 发送指标计算请求
     * @param quotaTemp 指标模板
     * @param computCal 计算时间点
     * @param iComputStrategy 计算策略
     */
    public synchronized void sendCalculateComm (QuotaTemp quotaTemp,
                                                         Calendar computCal, IQuotaComputStrategy iComputStrategy) {
        QuotaComputTask quotaComputTask = new QuotaComputTask();
        quotaComputTask.setComputCal(computCal);
        quotaComputTask.setAssignMillions(System.currentTimeMillis());
        quotaComputTask.setiQuotaComputStrategy(iComputStrategy);
        quotaComputTask.setQuotaTemp(quotaTemp);
        QuotaComputHelper._instance.submit(quotaComputTask);
    }


}
