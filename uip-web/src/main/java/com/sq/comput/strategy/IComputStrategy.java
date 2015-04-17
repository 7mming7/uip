package com.sq.comput.strategy;

import com.sq.comput.domain.IndicatorTemp;

import java.util.Calendar;

/**
 * 指标计算策略.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public interface IComputStrategy {

    /** 执行指标计算->根据不同的实现返回计算结果 */
    public Object execIndiComput(IndicatorTemp indicatorTemp, Calendar computCal);
}
