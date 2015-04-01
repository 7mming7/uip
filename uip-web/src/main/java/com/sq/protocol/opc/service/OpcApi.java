package com.sq.protocol.opc.service;

import org.jinterop.dcom.core.JIVariant;

import java.util.List;

/**
 * opc服务接口.
 * User: shuiqing
 * Date: 2015/3/27
 * Time: 9:54
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface OpcApi<T> {

    /**
     * 初始化客户端，
     * 1.加载客户端的配置信息，
     * 2.连接server。
     * 2014年11月1日 下午2:24:48 shuiqing PM 添加此方法
     * @return Connect的对象
     */
    public abstract T connect();

    /**
     * 关闭opc连接
     * @param t opc client
     */
    public abstract void closeConnection(T t);
}
