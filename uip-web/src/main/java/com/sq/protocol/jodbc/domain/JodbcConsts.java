package com.sq.protocol.jodbc.domain;

/**
 * 外接数据库常量.
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 14:21
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface JodbcConsts {

    /** ======================
     *    外接数据库
     *      1、sql server
     *      2、mysql
     */
    public static final int DB_SQLSERVER = 1;

    public static final int DB_MYSQL = 2;

    /** ======================
     *   接口系统编号
     *      1、opc
     *      2、odbc 地磅
     */
    public static final int SYS_OPC_DCS = 1;

    public static final int SYS_ODBC_LOADOMETER = 2;

    public static final int SYS_OPC_ECS = 3;
}
