package com.sq.comput.domain;

/**
 * 指标计算相关常量.
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
public interface IndicatorConsts {

    /** ======================
     *    数据来源
     *      1、录入
     *      2、计算
     *      3、接口
     */
    public static final int DATASOURCE_ENTRY = 1;

    public static final int DATASOURCE_CALCULATE = 2;

    public static final int DATASOURCE_INTERFACE = 3;

    /** ======================
     *    指标计算类型
     *      1、元指标
     *      2、累计指标
     *      3、库存指标
     */
    public static final int CALTYPE_ORIGINAL = 1;

    public static final int CALTYPE_CUMULATIVE = 2;

    public static final int CALTYPE_INVENTORY = 3;

    /** ======================
     *    数据获取频率
     *      1、小时
     *      2、日
     *      3、周
     *      4、月
     *      5、季度
     *      6、年
     */
    public static final int FETCH_CYCLE_HOUR = 1;

    public static final int FETCH_CYCLE_DAY = 2;

    public static final int FETCH_CYCLE_WEEK = 3;

    public static final int FETCH_CYCLE_Month = 4;

    public static final int FETCH_CYCLE_Quarter = 5;

    public static final int FETCH_CYCLE_Year = 6;


    /** ======================
     *     数据类型
     *        1、string
     *        2、double
     *
     */
    public static final int VALUE_TYPE_STRING = 1;

    public static final int VALUE_TYPE_DOUBLE = 2;

    /** ======================
     *     表达式类型
     *        1、原生不带参数
     *        2、动态参数计算
     *
     */
    public static final int EXPRESSION_NATIVE = 1;

    public static final int EXPRESSION_DYNAMIC = 2;

    /** ======================
     *     表达式类型
     *        1、原生不带参数
     *        2、动态参数计算
     *
     */
    public static final int LIMIT_TYPE_UPPER = 1;

    public static final int LIMIT_TYPE_LOWER = 2;

}
