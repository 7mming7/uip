package com.sq.loadometer_nj.controller;

import com.alibaba.fastjson.JSON;
import com.sq.inject.annotation.BaseComponent;
import com.sq.loadometer_nj.domain.Trade;
import com.sq.loadometer_nj.domain.TradeDataPushDto;
import com.sq.loadometer_nj.service.TradeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 地磅服务controller
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午5:35
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
/*@Controller*/
public class LoadmeterMainController {

    private static final Logger log = LoggerFactory.getLogger(LoadmeterMainController.class);

    @Autowired
    @BaseComponent
    private TradeDataService tradeDataService;

    /**
     * 查询地磅当日的数据
     * @param pointDay 指定的日期
     * @return 查询到得指定日期的地磅的流水数据
     */
    @ResponseBody
    @RequestMapping("loadmeter/tradeDataAllDay.do")
    public String tradeDataAllDay(@RequestParam String pointDay){
        TradeDataPushDto tradeDataPushDto = new TradeDataPushDto();
        List<Trade> tradeList = tradeDataService.fetchTradeDataByPointDay(pointDay);
        tradeDataPushDto.setSuccess(true);
        tradeDataPushDto.setTradeDataPushDtoList(tradeList);
        return JSON.toJSONString(tradeDataPushDto);
    }

}
