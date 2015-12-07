package com.sq.loadometer.controller;

import com.sq.loadometer.domain.Trade;
import com.sq.loadometer.service.TradeDataService;
import com.sq.util.StringUtils;
import com.sq.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 地磅controller
 * User: shuiqing
 * Date: 15/12/7
 * Time: 上午11:14
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
public class LoadometerController{

    private static final Logger log = LoggerFactory.getLogger(LoadometerController.class);

    @Autowired
    private TradeDataService tradeDataService;

    /**
     * 同步指定时间段内的地磅数据
     * @param betTwoDay 时间段 20151201-20151212
     * @return
     */
    @ResponseBody
    @RequestMapping("loadometer/syncTradeData.do")
    public String syncTradeData(@RequestParam String betTwoDay) {
        if(StringUtils.isBlank(betTwoDay)) {
           return null;
        }

        String[] betTwoDayArr = new String[2];
        if (betTwoDay.contains("-")) {
            betTwoDayArr = betTwoDay.split("-");
        } else {
            betTwoDayArr[0] = betTwoDay;
            betTwoDayArr[1] = betTwoDay;
        }

        for (Integer i=Integer.parseInt(betTwoDayArr[0]);i<=Integer.parseInt(betTwoDayArr[1]);i++) {
            System.out.println(i);
            tradeDataService.removeCurrDayTradeData(i.toString());
            tradeDataService.insertCurrDayTradeData(i.toString());
        }

        return null;
    }
}
