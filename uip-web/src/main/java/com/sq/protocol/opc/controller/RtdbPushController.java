package com.sq.protocol.opc.controller;

import com.alibaba.fastjson.JSON;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.domain.RtOrignalDataDto;
import com.sq.protocol.opc.service.RtdbPushService;
import com.sq.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时数据服务controller
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午2:00
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
public class RtdbPushController {

    private static final Logger log = LoggerFactory.getLogger(RtdbPushController.class);

    @Autowired
    private RtdbPushService rtdbPushService;

    /** 测点缓存 */
    public static Map<String, MesuringPoint> mesuringPointMap = new HashMap<String,MesuringPoint>();

    /**
     * 南大先腾实时数据推送
     * @param postRtCode 请求的测点编码的列表
     * @return 获取的实时数据
     */
    @ResponseBody
    @RequestMapping("rtdb/rtdbPush.do")
    public String ndxtRtdbPush (@RequestParam String postRtCode) {
        log.debug(postRtCode);
        String serialString = null;
        RtOrignalDataDto rtOrignalDataDto = new RtOrignalDataDto();

        String msg = "";
        if (StringUtils.isBlank(postRtCode)) {
            msg = "ERROR CODE 1: 传入的测点请求编码为空.";
            rtOrignalDataDto.setMsg(msg);
            rtOrignalDataDto.setSuccess(false);
            serialString = JSON.toJSONString(rtOrignalDataDto);
            return serialString;
        }

        List<MesuringPoint> mesuringPointList = new ArrayList<MesuringPoint>();
        String[] codeArr = postRtCode.split(",");
        for (String postCode:codeArr) {
            MesuringPoint mp = mesuringPointMap.get(postCode);
            if (null == mp) {
                mp = rtdbPushService.fetchMpByPostCode(postCode);
                if (null == mp) {
                    msg = msg + "," + postCode + "不是系统测点编码，请查证.";
                    continue;
                }
                mesuringPointMap.put(postCode,mp);
            }

            mesuringPointList.add(mp);
        }

        List<OriginalData> originalDataList = rtdbPushService.fetchOriDataList(mesuringPointList);
        rtOrignalDataDto.setSuccess(true);
        rtOrignalDataDto.setOriginalDataList(originalDataList);
        log.debug(JSON.toJSONString(rtOrignalDataDto));

        return JSON.toJSONString(rtOrignalDataDto);
    }
}
