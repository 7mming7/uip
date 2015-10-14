package com.sq.quota.service;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.OrCondition;
import com.sq.entity.search.condition.SearchFilterHelper;
import com.sq.inject.annotation.BaseComponent;
import com.sq.quota.component.DimensionComparator;
import com.sq.quota.component.QuotaComputHelper;
import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.repository.QuotaInstanceRepository;
import com.sq.quota.repository.QuotaTempRepository;
import com.sq.quota.strategy.IQuotaComputStrategy;
import com.sq.quota.strategy.InterfaceQuotaStrategy;
import com.sq.quota.strategy.PrimaryQuotaStrategy;
import com.sq.quota.strategy.QuotaComputTask;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import net.sourceforge.jeval.EvaluationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 指标计算业务类
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
public class QuotaComputInsService extends BaseService<QuotaInstance,Long> {

    private static final Logger log = LoggerFactory.getLogger(QuotaComputInsService.class);

    @Autowired
    @BaseComponent
    private QuotaInstanceRepository quotaInstanceRepository;

    @Autowired
    private QuotaTempRepository quotaTempRepository;

    /** 单个计算轮回中的超时时限 */
    public static Long computWaitTimeOutValue = 10l;

    /** 指标模板缓存 */
    public static Map<String,QuotaTemp> quotaTempMapCache = new HashMap<String,QuotaTemp>();

    /**
     * 初始化操作
     *     1、缓存指标模板
     *     2、更新指标模板的指标模板基础表达式
     */
    public void reloadQuotaCalculateExp() {
        cacheQuotaTemp();
        updateQuotaNativeExp();
    }

    /**
     * 缓存指标模板
     */
    public void cacheQuotaTemp () {
        List<QuotaTemp> quotaTempList = quotaTempRepository.findAll();
        for (QuotaTemp quotaTemp:quotaTempList) {
            quotaTempMapCache.put(quotaTemp.getIndicatorCode(), quotaTemp);
        }
    }

    /**
     * 更新指标的基础表达式
     */
    public void updateQuotaNativeExp () {
        List<QuotaTemp> quotaTempList = new LinkedList<QuotaTemp>();
        for (Map.Entry<String, QuotaTemp> entry : quotaTempMapCache.entrySet()) {
            if (entry.getValue().getDataSource() != QuotaConsts.DATASOURCE_CALCULATE) {
                continue;
            }
            String nativeExpression = generateNativeExpression(entry.getValue().getCalculateExpression());
            entry.getValue().setGernaterdNativeExpression(nativeExpression);
            quotaTempList.add(entry.getValue());
        }
        quotaTempRepository.save(quotaTempList);
    }


    /**
     * 生成最低级别的计算表达式
     * @param exp 计算表达式
     * @return 生成的指标表达式
     */
    public String generateNativeExpression (String exp) {
        if (exp == null) return null;
        List<String> variableList = QuotaComputHelper.getVariableList(exp, QuotaComputHelper.getEvaluatorInstance());

        boolean expStatusflag = true;//表达式状态，true表示已经是基础表达式，没有关联的计算指标了
        for (String variable:variableList) {
            //生成native expression时校验关联指标编码所对应的指标模板的存在性
            //   判断表达式中的编码是否存在，留到具体的计算处统一校验
            QuotaTemp quotaTemp = quotaTempMapCache.get(variable);
            if (null == quotaTemp) {
                return null;//关联指标不存在，直接退出
            }
            if (quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_CALCULATE) {
                String replaceString = quotaTemp.getCalculateExpression();
                String needReplaceString = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;
                exp = exp.replace(needReplaceString,replaceString);
                expStatusflag = false;
            }
        }

        if (expStatusflag || exp == null) {
            return exp;
        } else {
            return generateNativeExpression(exp);
        }
    }

    /**
     * 接口数据汇集到系统的最小维度小时级
     * @param computCal 计算时间
     */
    public void interfaceDataGather (Calendar computCal) {

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, QuotaConsts.DATASOURCE_INTERFACE)
                .addSearchFilter("calFrequency", MatchType.EQ, QuotaConsts.CAL_FREQUENCY_HOUR);

        List<QuotaTemp> quotaTempList = this.quotaTempRepository.findAll(searchable).getContent();

        for (QuotaTemp quotaTemp : quotaTempList) {
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
        QuotaComputHelper.fetchThreadPooSingleInstance().submit(quotaComputTask);
    }


    /**
     * 接口指标日数据汇集
     * @param computCal 计算日期
     */
    public void interfaceIndicatorDataGater (Calendar computCal) {

        LinkedBlockingQueue<QuotaTemp> waitComputQuotaQueue = new LinkedBlockingQueue<QuotaTemp>();

        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, QuotaConsts.DATASOURCE_INTERFACE)
                .addSearchFilter("calFrequency", MatchType.GTE, QuotaConsts.CAL_FREQUENCY_DAY);
        List<QuotaTemp> quotaTempList = quotaTempRepository.findAll(searchable).getContent();
        Collections.sort(quotaTempList, new DimensionComparator());

        for (QuotaTemp quotaTemp:quotaTempList) {
            waitComputQuotaQueue.add(quotaTemp);
        }

        stepSendComputRequest(waitComputQuotaQueue, QuotaConsts.CAL_FREQUENCY_DAY, computCal);
    }

    /**
     * 逐步发送指标计算请求
     * @param waitComputQuotaQueue
     * @param currCalFrequency
     * @param computCal
     */
    public void stepSendComputRequest (LinkedBlockingQueue<QuotaTemp> waitComputQuotaQueue, int currCalFrequency, Calendar computCal) {
        while (!waitComputQuotaQueue.isEmpty()) {
            QuotaTemp quotaTemp = waitComputQuotaQueue.poll();
            if (quotaTemp.getCalFrequency() != currCalFrequency) {
                currCalFrequency = quotaTemp.getCalFrequency();
                while (QuotaComputHelper.computThreadQueue.isEmpty()
                        || QuotaComputHelper.fetchThreadPooSingleInstance().getActiveCount() == 0) {
                    continue;
                }
            }
            if (quotaTemp.getCalFrequency() == QuotaConsts.CAL_FREQUENCY_HOUR) {
                List<Calendar> hourCalList = DateUtil.get24Hours(computCal);
                for (Calendar hourCal:hourCalList) {
                    sendCalculateComm(quotaTemp, computCal, new PrimaryQuotaStrategy());
                }
            } else {
                sendCalculateComm(quotaTemp, computCal, new PrimaryQuotaStrategy());
            }
        }
    }

    /**
     * 找出指标集合的关联指标
     */
    public List<QuotaTemp> searchAssociatedQuotaTemp (List<String> calculateStringList) {
        Assert.notEmpty(calculateStringList, "searchAssociatedQuotaTemp需要查询关联指标的参数指标不存在.");
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("dataSource", MatchType.EQ, QuotaConsts.DATASOURCE_CALCULATE);
        OrCondition orCondition = new OrCondition();
        for (String quotaTempStr:calculateStringList) {
            orCondition.add(
                    SearchFilterHelper.newCondition("gernaterdNativeExpression",
                            MatchType.LIKE, "%#{" + quotaTempStr + "}%"));
        }
        searchable.addSearchFilter(orCondition);
        return quotaTempRepository.findAll(searchable).getContent();
    }

    /**
     * 删除需要重计算的指标以及关联指标实例
     */
    public void deleteNeedReComputIndicator(Calendar computCal, List<QuotaTemp> associatedQuotaTempList){
        if (associatedQuotaTempList.isEmpty()) return;
        List<String> indicatorCodeList = new ArrayList<String>();
        for(QuotaTemp quotaTemp:associatedQuotaTempList) {
            indicatorCodeList.add(quotaTemp.getIndicatorCode());
        }

        int startComputDateNum = Integer.parseInt(DateUtil.formatCalendar(computCal, DateUtil.DATE_FORMAT_DAFAULT));
        Searchable deleteSearchable = Searchable.newSearchable()
                .addSearchFilter("statDateNum", MatchType.GTE, startComputDateNum)
                .addSearchFilter("dataSource", MatchType.EQ, QuotaConsts.DATASOURCE_CALCULATE)
                .addSearchFilter("indicatorCode",MatchType.IN, indicatorCodeList);
        quotaInstanceRepository.delete(quotaInstanceRepository.findAll(deleteSearchable).getContent());
    }

    /**
     * 计算指标重计算
     *     在指定计算日期至当前日期的时间范围内，对传入的指标的所有关联指标做重计算
     * @param computCal
     * @param quotaTempList
     */
    public void reComputQuota (Calendar computCal, List<QuotaTemp> quotaTempList) {

        LinkedBlockingQueue<QuotaTemp> waitComputQuotaQueue = new LinkedBlockingQueue<QuotaTemp>();

        List<String> calculateStringList = new ArrayList<String>();
        for (QuotaTemp quotaTemp:quotaTempList) {
            if (quotaTemp.getDataSource() == IndicatorConsts.DATASOURCE_CALCULATE
                    || quotaTemp.getDataSource() == IndicatorConsts.DATASOURCE_INTERFACE ) {
                calculateStringList.addAll(QuotaComputHelper.getVariableList(quotaTemp.getGernaterdNativeExpression(),
                        QuotaComputHelper.getEvaluatorInstance()));
            } else if (quotaTemp.getDataSource() == IndicatorConsts.DATASOURCE_ENTRY) {
                calculateStringList.add(quotaTemp.getIndicatorCode());
            }
        }

        List<QuotaTemp> associatedQuotaTempList = searchAssociatedQuotaTemp(calculateStringList);

        //删除计算指标的关联指标
        deleteNeedReComputIndicator(computCal, associatedQuotaTempList);
        Collections.sort(associatedQuotaTempList, new DimensionComparator());

        List<Calendar> calendarList = DateUtil.dayListSinceCal(computCal);

        for(Calendar computCalTemp:calendarList) {
            stepSendComputRequest(waitComputQuotaQueue, QuotaConsts.CAL_FREQUENCY_HOUR, computCalTemp);
        }
    }
}
