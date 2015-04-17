package com.sq.comput.strategy;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.service.IndiComputService;
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
public class IndiComputThread extends Thread {

    private Logger log = LoggerFactory.getLogger(IndiComputThread.class);

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

    public IndiComputThread (IComputStrategy iComputStrategy, Calendar computCal) {
        this.iComputStrategy = iComputStrategy;
        this.computCal = computCal;
    }

    @Override
    public void run() {
        log.info("Module Comput " + iComputStrategy.getClass().getName() + ":发送计算请求.");
        IndicatorInstance indicatorInstance = new IndicatorInstance(indicatorTemp);
        Object computResult = iComputStrategy.execIndiComput(indicatorTemp, computCal);
        if (computResult instanceof String) {
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_STRING);
            indicatorInstance.setStringValue(computResult.toString());
        } else {
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_DOUBLE);
            indicatorInstance.setFloatValue(IndicatorConsts.VALUE_TYPE_DOUBLE);
        }
        indiComputService.save(indicatorInstance);
    }
}
