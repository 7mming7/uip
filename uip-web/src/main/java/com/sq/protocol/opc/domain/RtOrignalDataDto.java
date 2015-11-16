package com.sq.protocol.opc.domain;

import com.sq.protocol.opc.domain.OriginalData;

import java.io.Serializable;
import java.util.List;

/**
 * 实时测点数据DTO
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午3:54
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class RtOrignalDataDto implements Serializable {

    private static final long serialVersionUID = 7732213403581547349L;

    private boolean success;

    private String msg;

    private List<OriginalData> originalDataList;

    public List<OriginalData> getOriginalDataList() {
        return originalDataList;
    }

    public void setOriginalDataList(List<OriginalData> originalDataList) {
        this.originalDataList = originalDataList;
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
