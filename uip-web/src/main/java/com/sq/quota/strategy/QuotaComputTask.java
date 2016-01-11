package com.sq.quota.strategy;

import com.sq.quota.domain.QuotaConsts;
import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.service.QuotaComputInsService;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 指标计算独立线程.
 * 不同指标不同的计算方法的优先级通过线程内部定义的权重来确定.
 * User: shuiqing
 * Date: 2015/4/17
 * Time: 14:30
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class QuotaComputTask implements Runnable {

    private Logger log = LoggerFactory.getLogger(QuotaComputTask.class);

    /**
     * 由于Thread非spring启动时实例化，而是根据具体的逻辑动态实例化，所以需要通过此方式从spring的context中获取相应的bean.
     */
    private QuotaComputInsService quotaComputService = SpringUtils.getBean(QuotaComputInsService.class);

    /** 信号量 指定来自具体某一算法 */
    private String semaphore;

    /** 分配该线程时的秒数 */
    private long assignMillions;

    /** 权重 用来在线程池中确定线程执行的优先级 */
    private int weight;

    private Calendar computCal;

    /** 指标计算真正执行的算法接口，具体实现需要在实例化指标计算线程的时候指定 */
    private IQuotaComputStrategy iQuotaComputStrategy;

    private QuotaTemp quotaTemp;

    public QuotaComputTask(){}

    public QuotaComputTask(IQuotaComputStrategy iQuotaComputStrategy, Calendar computCal) {
        this.iQuotaComputStrategy = iQuotaComputStrategy;
        this.computCal = computCal;
    }

    public String getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(String semaphore) {
        this.semaphore = semaphore;
    }

    public long getAssignMillions() {
        return assignMillions;
    }

    public void setAssignMillions(long assignMillions) {
        this.assignMillions = assignMillions;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Calendar getComputCal() {
        return computCal;
    }

    public void setComputCal(Calendar computCal) {
        this.computCal = computCal;
    }

    public IQuotaComputStrategy getiQuotaComputStrategy() {
        return iQuotaComputStrategy;
    }

    public void setiQuotaComputStrategy(IQuotaComputStrategy iQuotaComputStrategy) {
        this.iQuotaComputStrategy = iQuotaComputStrategy;
    }

    public QuotaTemp getQuotaTemp() {
        return quotaTemp;
    }

    public void setQuotaTemp(QuotaTemp quotaTemp) {
        this.quotaTemp = quotaTemp;
    }

    @Override
    public void run() {
        log.error("Module Comput "
                + quotaTemp.getIndicatorCode()
                + " ---- "
                + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_YMDH)
                + " start comput comd.");
        QuotaInstance quotaInstance = new QuotaInstance(quotaTemp);

        Calendar tempCal = (Calendar) computCal.clone();
        Object computResult = iQuotaComputStrategy.execIndiComput(quotaTemp, tempCal);

        log.error(quotaTemp.getIndicatorCode() + "，"
                + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_YMDH)
                + " 计算结果为：-- " + (null != computResult ? computResult.toString() : "null"));

        if (computResult instanceof String) {
            quotaInstance.setValueType(QuotaConsts.VALUE_TYPE_STRING);
            quotaInstance.setStringValue(computResult.toString());
        } else {
            quotaInstance.setValueType(QuotaConsts.VALUE_TYPE_DOUBLE);
            quotaInstance.setFloatValue(computResult != null ? Double.parseDouble(computResult.toString()):null);
        }

        Calendar tempComputCal = (Calendar) computCal.clone();
        if(iQuotaComputStrategy instanceof InterfaceQuotaStrategy) {
            if (null == computResult) return;
        }
        quotaInstance.setInstanceTime(tempComputCal.getTime());
        quotaInstance.setCreateTime(Calendar.getInstance());
        quotaInstance.setStatDateNum(Integer.parseInt(DateUtil.formatCalendar(tempComputCal, DateUtil.DATE_FORMAT_DAFAULT)));
        quotaComputService.save(quotaInstance);
    }
}
