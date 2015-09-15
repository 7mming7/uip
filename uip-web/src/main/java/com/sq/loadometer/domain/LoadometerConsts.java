package com.sq.loadometer.domain;

/**
 * 地磅常量
 * User: shuiqing
 * Date: 2015/9/15
 * Time: 10:04
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public interface LoadometerConsts {

    /** ======================
     *    外接数据库
     *      1、sql server
     *      2、mysql
     */
    public static final int DB_SQLSERVER = 1;

    public static final int DB_MYSQL = 2;

    /** ======================
     *   接口系统编号
     *      1、opc dcs
     *      2、odbc 地磅
     *      3、opc ecs
     *      4、opc sly
     */
    public static final int SYS_OPC_DCS = 1;

    public static final int SYS_ODBC_LOADOMETER = 2;

    public static final int SYS_OPC_ECS = 3;

    public static final int SYS_OPC_SLY = 4;
}
