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
        updateQuotaExp();
    }

    /**
     * 缓存指标模板
     */
    public void cacheQuotaTemp () {
        List<QuotaTemp> quotaTempList = quotaTempRepository.findAll();
        for (QuotaTemp quotaTemp:quotaTempList) {
            System.out.println("Cache quotaTemp code:" + quotaTemp.getIndicatorCode());
            quotaTempMapCache.put(quotaTemp.getIndicatorCode(), quotaTemp);
        }
    }

    /**
     * 更新指标的基础表达式
     */
    public void updateQuotaExp () {
        List<QuotaTemp> quotaTempList = new LinkedList<QuotaTemp>();
        for (Map.Entry<String, QuotaTemp> entry : quotaTempMapCache.entrySet()) {
            if (entry.getValue().getDataSource() != QuotaConsts.DATASOURCE_CALCULATE) {
                continue;
            }
            QuotaTemp quotaTemp = entry.getValue();
            System.out.println(quotaTemp.getIndicatorCode() + "-----" + quotaTemp.getCalculateExpression());
            String calExp = new String(quotaTemp.getCalculateExpression());
            String nativeExpression = generateNativeExpression(quotaTemp);
            entry.getValue().setGernaterdNativeExpression(nativeExpression);
            entry.getValue().setCalculateExpression(calExp);

            /*String mathExpression = generateMathExpression(entry.getValue().getCalculateExpression(),
                    entry.getValue().getFetchCycle());
            entry.getValue().setMathExpression(mathExpression);*/
            log.error("CalculateExpression:" + entry.getValue().getCalculateExpression()
                    + ",nativeExpression:" + nativeExpression);
            quotaTempList.add(entry.getValue());
        }
        quotaTempRepository.save(quotaTempList);
    }


    /**
     * 生成最低级别的计算表达式
     * @return 生成的指标表达式
     */
    public String generateNativeExpression (QuotaTemp quota) {
        log.error("QuotaTemp :" + quota.getIndicatorCode() + " generateNativeExpression.Exp:" + quota.getCalculateExpression());
        if (quota.getCalculateExpression() == null) return null;

        if (quota.getCalculateExpression().contains("dateTime")) {
            return quota.getCalculateExpression();
        }

        List<String> variableList = QuotaComputHelper.getVariableList(quota.getCalculateExpression(), QuotaComputHelper.getEvaluatorInstance());

        boolean expStatusflag = true;//表达式状态，true表示已经是基础表达式，没有关联的计算指标了
        for (String variable:variableList) {
            //生成native expression时校验关联指标编码所对应的指标模板的存在性
            //判断表达式中的编码是否存在，留到具体的计算处统一校验
            QuotaTemp quotaTemp = quotaTempMapCache.get(variable);
            if (null == quotaTemp) {
                log.error("generateNativeExpression-->>QuotaTemp :" + variable + " 不存在.");
                return null;//关联指标不存在，直接退出
            }
            if (quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_CALCULATE) {
                String replaceString = quotaTemp.getCalculateExpression();
                String needReplaceString = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;
                quota.setCalculateExpression(quota.getCalculateExpression().replace(needReplaceString, replaceString));
                quota.setSemaphore(quotaTemp.getSemaphore() + 1);
                expStatusflag = false;
            }
        }

        if (expStatusflag || quota.getCalculateExpression() == null) {
            return quota.getCalculateExpression();
        } else {
            return generateNativeExpression(quota);
        }
    }

    /**
     * 生成数学计算指标表达式
     * @param exp 配置在模板上的表达式
     * @return 生成的数学计算表达式
     */
    /*public String generateMathExpression (String exp, int fetchCycle){
        if (exp == null) return null;

        List<String> variableList = QuotaComputHelper.getVariableList(exp, QuotaComputHelper.getEvaluatorInstance());
        for (String variable:variableList) {
            //判断表达式中的编码是否存在，留到具体的计算处统一校验
            QuotaTemp quotaTemp = quotaTempMapCache.get(variable);
            if (null == quotaTemp ) {
                return null;//关联指标不存在，直接退出
            }
            if (quotaTemp.getFetchCycle() == fetchCycle && quotaTemp.getDataSource() == QuotaConsts.DATASOURCE_CALCULATE) {
                String replaceString = quotaTemp.getCalculateExpression();
                String needReplaceString = EvaluationConstants.OPEN_VARIABLE + variable + EvaluationConstants.CLOSED_BRACE;
                exp = exp.replace(needReplaceString,replaceString);
            }
        }

        return exp;
    }*/

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
        QuotaComputHelper._instance.execute(quotaComputTask);
        /*QuotaComputHelper.fetchThreadPooSingleInstance().submit(quotaComputTask);*/
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

        stepSendComputRequest(waitComputQuotaQueue, QuotaConsts.CAL_FREQUENCY_DAY,QuotaConsts.FETCH_CYCLE_HOUR, computCal);
    }

    /**
     * 逐步发送指标计算请求
     * @param waitComputQuotaQueue
     * @param currCalFrequency
     * @param computCal
     */
    public synchronized void stepSendComputRequest (LinkedBlockingQueue<QuotaTemp> waitComputQuotaQueue,
                                       int currCalFrequency,
                                       int fetchCycle,
                                       Calendar computCal) {
        int initSemaphore = 0;
        while (!waitComputQuotaQueue.isEmpty()) {
            QuotaTemp quotaTemp = waitComputQuotaQueue.poll();
            if (quotaTemp.getCalFrequency() != currCalFrequency
                    || quotaTemp.getFetchCycle() != fetchCycle
                    || quotaTemp.getSemaphore() != initSemaphore) {
                currCalFrequency = quotaTemp.getCalFrequency();
                fetchCycle = quotaTemp.getFetchCycle();
                initSemaphore = quotaTemp.getSemaphore();
                QuotaComputHelper._instance.shutdown();

                while (true) {
                    System.out.println("Computcal:" + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULTYMDHMS)
                            + ",Active count:" + QuotaComputHelper._instance.getActiveCount()
                            + ",currCalFrequency:" + currCalFrequency
                            + ",currFetchCycle:" + fetchCycle
                            + ",isTerminating:" + QuotaComputHelper._instance.isTerminating()
                            + ",isTerminating:" + QuotaComputHelper._instance.isTerminated());
                    if (!QuotaComputHelper._instance.isTerminating() && QuotaComputHelper._instance.isTerminated()) {
                        System.out.println("Instance thread pool!");
                        QuotaComputHelper.fetchThreadPooSingleInstance();
                        break;
                    }
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException e) {
                        log.error("Thread sleep error!");
                    }
                }
            }
            if (quotaTemp.getCalFrequency() == QuotaConsts.CAL_FREQUENCY_HOUR) {
                List<Calendar> hourCalList = DateUtil.get24Hours(computCal);
                for (Calendar hourCal:hourCalList) {
                    sendCalculateComm(quotaTemp, hourCal, new PrimaryQuotaStrategy());
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
                            MatchType.LIKE, "%" + quotaTempStr + "%"));
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
        System.out.println(quotaTempList.size());
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
        List<QuotaTemp> sortQuotaTempList = new ArrayList<QuotaTemp>(associatedQuotaTempList);

        //删除计算指标的关联指标
        deleteNeedReComputIndicator(computCal, associatedQuotaTempList);

        Collections.sort(sortQuotaTempList, new DimensionComparator());
        for (QuotaTemp quotaTemp:sortQuotaTempList) {
            System.out.println("Sort quotaTemp: CAL_FREQUENCY->" + quotaTemp.getCalFrequency() + ",FETCH_CYCLE->" + quotaTemp.getFetchCycle());
        }

        List<Calendar> calendarList = DateUtil.dayListSinceCal(computCal);

        for(Calendar computCalTemp:calendarList) {
            System.out.println("sortQuotaTempList size: " + sortQuotaTempList.size());
            System.out.println("Start send comput request computCal: " + DateUtil.formatCalendar(computCalTemp));
            for (QuotaTemp quotaTemp:sortQuotaTempList) {
                System.out.println("Need Comput QuotaTemp: " + quotaTemp.getIndicatorCode() + ", mathExpression:" + quotaTemp.getMathExpression());
            }
            waitComputQuotaQueue.addAll(associatedQuotaTempList);
            stepSendComputRequest(waitComputQuotaQueue, QuotaConsts.CAL_FREQUENCY_HOUR, QuotaConsts.FETCH_CYCLE_HOUR, computCalTemp);
        }
    }
}
