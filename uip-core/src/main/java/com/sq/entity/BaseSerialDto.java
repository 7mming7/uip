package com.sq.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 序列化基础对象
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午5:33
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class BaseSerialDto<T> implements Serializable {

    private static final long serialVersionUID = -832806484440275497L;

    private boolean success;

    private String msg;

    private List<T> dataList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
