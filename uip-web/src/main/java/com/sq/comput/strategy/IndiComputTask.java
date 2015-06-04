package com.sq.comput.strategy;

import com.sq.comput.component.ComputHelper;
import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.service.IndiComputService;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

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
public class IndiComputTask implements Callable<IndicatorInstance> {

    private Logger log = LoggerFactory.getLogger(IndiComputTask.class);

    /**
     * 由于Thread非spring启动时实例化，而是根据具体的逻辑动态实例化，所以需要通过此方式从spring的context中获取相应的bean.
     */
    private IndiComputService indiComputService = SpringUtils.getBean(IndiComputService.class);

    /** 信号量 指定来自具体某一算法 */
    private String semaphore;

    /** 分配该线程时的秒数 */
    private long assignMillions;

    /** 权重 用来在线程池中确定线程执行的优先级 */
    private int weight;

    private Calendar computCal;

    /** 指标计算真正执行的算法接口，具体实现需要在实例化指标计算线程的时候指定 */
    private IComputStrategy iComputStrategy;

    private IndicatorTemp indicatorTemp;

    public IndiComputTask(){}

    public IndiComputTask(IComputStrategy iComputStrategy, Calendar computCal) {
        this.iComputStrategy = iComputStrategy;
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

    public IComputStrategy getiComputStrategy() {
        return iComputStrategy;
    }

    public void setiComputStrategy(IComputStrategy iComputStrategy) {
        this.iComputStrategy = iComputStrategy;
    }

    public IndicatorTemp getIndicatorTemp() {
        return indicatorTemp;
    }

    public void setIndicatorTemp(IndicatorTemp indicatorTemp) {
        this.indicatorTemp = indicatorTemp;
    }

    @Override
    public IndicatorInstance call() throws Exception {
        log.info("Module Comput " + indicatorTemp.getIndicatorCode() + ":发送计算请求.");
        IndicatorInstance indicatorInstance = new IndicatorInstance(indicatorTemp);
        indicatorInstance.setInstanceTime(computCal);
        indicatorInstance.setStatDateNum(Integer.parseInt(DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULT)));
        Calendar tempCal = (Calendar) computCal.clone();
        Object computResult = iComputStrategy.execIndiComput(indicatorTemp, tempCal);
        if (null == computResult) {
            return null;
        }
        log.error(indicatorTemp.getIndicatorCode() + "，"
                + DateUtil.formatCalendar(computCal,DateUtil.DATE_FORMAT_DAFAULT)
                + "计算结果为：--" + (null != computResult ? computResult.toString() : "null"));
        if (computResult instanceof String) {
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_STRING);
            indicatorInstance.setStringValue(computResult.toString());
        } else {
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_DOUBLE);
            indicatorInstance.setFloatValue(Double.parseDouble(computResult.toString()));
        }
        indiComputService.saveAndFlush(indicatorInstance);
        synchronized(ComputHelper.threadCalculateMap) {
            ComputHelper.threadCalculateMap.put(indicatorTemp.getIndicatorCode(), IndicatorConsts.VALUE_TYPE_DOUBLE);
        }
        return indicatorInstance;
    }
}
