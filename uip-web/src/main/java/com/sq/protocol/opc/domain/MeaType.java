package com.sq.protocol.opc.domain;

import com.sq.exception.UnsupportedValueException;

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

    /**
     * 根据枚举的下标获取对应的枚举。如果没有找到对应的枚举，则返回null。
     * @param index 枚举的下标值
     * @return 根据下标找到的枚举。如果没有匹配的枚举，返回空指针null。
     */
    public static MeaType indexOf(int index) {
        for (MeaType item : MeaType.values()) {
            if (item.index == index) {
                return item;
            }
        }
        throw new UnsupportedValueException("枚举类型 EcoAnalysisStatus 不支持下标值 " + index);
    }

    /**
     * 根据枚举的字面值获取对应的枚举。如果没有找到对应的枚举，则返回null。
     * @param name 枚举的字面值
     * @return 根据字面值找到的枚举。如果没有匹配的枚举，返回空指针null。
     */
    public static MeaType nameOf(String name) {
        for (MeaType item : MeaType.values()) {
            if (item.toString().equals(name)) {
                return item;
            }
        }
        throw new UnsupportedValueException("枚举类型 EcoAnalysisStatus 不支持字面值 " + name);
    }
}
