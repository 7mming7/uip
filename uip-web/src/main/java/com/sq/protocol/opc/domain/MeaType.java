package com.sq.protocol.opc.domain;

/**
 * 测点类型.
 * User: shuiqing
 * Date: 2015/3/30
 * Time: 14:47
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public enum MeaType {


    /** 原始测点*/
    OriginalMea(1),

    LogicCalMea(2);

    /**
     * 下标值。
     */
    private int index;

    /**
     * 使用下标构造枚举。
     * 构造函数
     * @param index 用于构造枚举的下标。
     */
    private MeaType(int index) {
        this.index = index;
    }

    /**
     * 返回当前枚举的下标。
     * @return 返回本枚举的下标值
     */
    public int index() {
        return index;
    }
}
