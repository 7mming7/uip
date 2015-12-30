package com.sq.quota.domain;

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
public interface QuotaConsts {

    /** ======================
     *    数据来源
     *      1、录入
     *      2、计算
     *      3、接口
     *      4、补录
     */
    public static final int DATASOURCE_ENTRY = 1;

    public static final int DATASOURCE_CALCULATE = 2;

    public static final int DATASOURCE_INTERFACE = 3;

    /** ======================
     *    指标计算类型
     *      1、元指标
     *      2、损耗指标
     *      3、库存指标
     */
    public static final int CALTYPE_ORIGINAL = 1;

    public static final int CALTYPE_LOSS = 2;

    public static final int CALTYPE_INVENTORY = 3;

    /** ======================
     *    计算维度
     *      1、半小时
     *      2、小时
     *      3、日
     *      4、周
     *      5、月
     *      6、季度
     *      7、年
     */
    public static final int FETCH_CYCLE_HALF_HOUR = 7;

    public static final int FETCH_CYCLE_HOUR = 1;

    public static final int FETCH_CYCLE_DAY = 2;

    public static final int FETCH_CYCLE_WEEK = 3;

    public static final int FETCH_CYCLE_Month = 4;

    public static final int FETCH_CYCLE_Quarter = 5;

    public static final int FETCH_CYCLE_Year = 6;

    /** ======================
     *    频率
     *      1、半小时
     *      2、小时
     *      3、日
     *      4、周
     *      5、月
     *      6、季度
     *      7、年
     */
    public static final int CAL_FREQUENCY_HALF_HOUR = 7;

    public static final int CAL_FREQUENCY_HOUR = 1;

    public static final int CAL_FREQUENCY_DAY = 2;

    public static final int CAL_FREQUENCY_WEEK = 3;

    public static final int CAL_FREQUENCY_Month = 4;

    public static final int CAL_FREQUENCY_Quarter = 5;

    public static final int CAL_FREQUENCY_Year = 6;


    /** ======================
     *     数据类型
     *        1、string
     *        2、double
     *
     */
    public static final int VALUE_TYPE_STRING = 1;

    public static final int VALUE_TYPE_DOUBLE = 2;

    public static final int VALUE_TYPE_SPACE = 3;

    /** ======================
     *     空值处理
     *        1、NULL
     *        2、0
     *
     */
    public static final int DOWITH_NULL_BENULL = 1;

    public static final int DOWITH_NULL_0 = 2;
}
