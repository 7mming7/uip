package com.sq.loadometer_nj.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 地磅数据DTO
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午6:39
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TradeDataPushDto implements Serializable {

    private static final long serialVersionUID = -3492839179696846710L;

    private boolean success;

    private String msg;

    private List<Trade> tradeDataPushDtoList;

    public List<Trade> getTradeDataPushDtoList() {
        return tradeDataPushDtoList;
    }

    public void setTradeDataPushDtoList(List<Trade> tradeDataPushDtoList) {
        this.tradeDataPushDtoList = tradeDataPushDtoList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
