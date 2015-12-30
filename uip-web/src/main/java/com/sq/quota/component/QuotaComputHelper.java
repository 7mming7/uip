package com.sq.quota.component;


import com.sq.quota.function.logical.DateTimeFunction;
import com.sq.quota.function.logical.InstantFunction;
import com.sq.quota.function.math.*;
import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.EvaluationHelper;
import net.sourceforge.jeval.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 指标计算辅助类.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 10:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class QuotaComputHelper {

    private static final Logger log = LoggerFactory.getLogger(QuotaComputHelper.class);

    private static Evaluator evaluator = null;

    /** 指标请求计算线程池的初始size */
    public static int indicatorThreadPoolSize = 100;

    /** 线程超时时长 */
    public static Long requestWaitTimeOutValue = 30l;

    /** 原子性的int值，用作指标计算的计数器（线程安全） */
    /*public static AtomicInteger computExecuteCounter = new AtomicInteger(0);*/

    /**
     * 同步Map对象记录了指标计算的进度
     */
    public static ConcurrentHashMap<String, Integer> threadCalculateMap = new ConcurrentHashMap<String, Integer>();

    /** 计算线程队列 */
    public static LinkedBlockingQueue<Runnable> computThreadQueue = new LinkedBlockingQueue<Runnable>();

    public static ThreadPoolExecutor _instance = null;

    static {
        loadLocalFunctions(getEvaluatorInstance());
        fetchThreadPooSingleInstance();
    }

    /**
     * 初始化指标计算线程池
     * 2014年10月23日 下午4:05:22 ShuiQing PM 添加此方法
     */
    public static synchronized ThreadPoolExecutor fetchThreadPooSingleInstance() {
        /**
         * 初始化请求线程池
         * @param indicatorThreadPoolSize coreThreadSize
         * @param Integer.MAX_VALUE maxThreadSize 取整数最大值
         * @param requestWaitTimeOutValue 当pool中的实际线程数大于coreThreadSize时，在池中的等待线程可以等待的时长，超过这个时长则丢弃这些空闲线程
         * @param TimeUnit.SECONDS 参数requestWaitTimeOutValue的单位
         * @param SynchronousQueue 同步队列，当有新的线程对象提交给线程池的时候，当前设计下coreThreadSize永远小于maxThreadSize，所以SynchronousQueue不会的等待，
         *         直接提交给线程池，新建一个idle的线程，等待coreThread中执行玩。
         * @param DiscardOldestPolicy 线程池的拒绝策略，当等待提交到线程池的线程对象超过缓冲队列的时候，将丢弃缓冲队列列头的对象。
         */
        _instance = new ThreadPoolExecutor(indicatorThreadPoolSize, Integer.MAX_VALUE, requestWaitTimeOutValue,
                TimeUnit.SECONDS, computThreadQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
        return _instance;
    }

    public static Evaluator getEvaluatorInstance () {
        if (null == evaluator ) {
            evaluator = new Evaluator();
        }
        return evaluator;
    }

    /**
     * 加载本地函数
     * 2014年10月27日 下午2:32:50 ShuiQing PM 添加此方法
     * @param evaluator 表达式执行器
     */
    public static void loadLocalFunctions (Evaluator evaluator) {
        evaluator.putFunction(new AvgFunction());
        evaluator.putFunction(new MaxAllFunction());
        evaluator.putFunction(new MinAllFunction());
        evaluator.putFunction(new SubFunction());
        evaluator.putFunction(new SumFunction());
        evaluator.putFunction(new MultiplyFunction());
        evaluator.putFunction(new DivideFunction());
        evaluator.putFunction(new PstFunction());
        evaluator.putFunction(new DateTimeFunction());
        evaluator.putFunction(new InstantFunction());
    }

    /**
     * 搜索出表达式中中配置的动态参数
     * 2014年10月26日 下午10:12:53 ShuiQing PM 添加此方法
     * @param expression 表达式copy
     * @param evaluator 表达式执行器
     * @return 动态参数列表
     */
    public static List<String> getVariableList (String expression,Evaluator evaluator) {
        List<String> variableList = new ArrayList<String>();
        if(null == expression) return variableList;
        int openIndex = expression.indexOf(EvaluationConstants.OPEN_VARIABLE);

        if (openIndex < 0) {
            return variableList;
        }

        String replacedExpression = expression;

        while (openIndex >= 0) {

            int closedIndex = -1;
            if (openIndex >= 0) {
                closedIndex = replacedExpression.indexOf(
                        EvaluationConstants.CLOSED_VARIABLE, openIndex + 1);
                if (closedIndex > openIndex) {

                    String variableName = replacedExpression.substring(
                            openIndex
                                    + EvaluationConstants.OPEN_VARIABLE
                                    .length(), closedIndex);

                    // Validate that the variable name is valid.
                    /*try {
                        evaluator.isValidName(variableName);
                    } catch (IllegalArgumentException iae) {
                        String msg = "获取动态参数失败,Invalid variable name of \"" + variableName + "\".";
                        log.error(msg, iae);
                    }*/
                    variableList.add(variableName);

                    String variableString = EvaluationConstants.OPEN_VARIABLE
                            + variableName
                            + EvaluationConstants.CLOSED_VARIABLE;

                    replacedExpression = EvaluationHelper.replaceAll(
                            replacedExpression, variableString, "");
                } else {
                    break;
                }
            }

            openIndex = replacedExpression.indexOf(
                    EvaluationConstants.OPEN_VARIABLE);
        }
        return variableList;
    }

    /**
     * 校验计算表达式是否符合规范
     * @param calculateExp 计算表达式
     * @return 校验表达式的结果
     */
    private boolean validCalculateExp(String calculateExp) {
        return true;
    }
}
