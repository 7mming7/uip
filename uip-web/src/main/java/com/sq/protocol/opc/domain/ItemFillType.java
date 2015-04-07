package com.sq.protocol.opc.domain;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/7
 * Time: 16:29
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public class ItemFillType {

    /** 通过连接opc服务读取item */
    public static final int AutoGenerate = 1;

    /** 从数据库中的记录表中查询 */
    public static final int DbRecord = 2;
}
