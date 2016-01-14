package com.sq.quota.controller;

import com.sq.quota.service.QuotaComputInsService;
import com.sq.util.DateUtil;
import com.sq.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * User: shuiqing
 * Date: 15/12/30
 * Time: 上午11:30
 * Email: annuus.sq@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
public class QuotaComputController {

    private static final Logger log = LoggerFactory.getLogger(QuotaComputController.class);

    @Autowired
    private QuotaComputInsService quotaComputInsService;

    /**
     * 同步指定时间段内的地磅数据
     * @param syncDate 时间点 201512301230
     * @return
     */
    @ResponseBody
    @RequestMapping("quotaComput/syncInterfaceData.do")
    public String syncInterfaceData(@RequestParam String syncDate) {
        if(StringUtils.isBlank(syncDate)) {
            return null;
        }
        try {
            Calendar syncCal = DateUtil.stringToCalendar(syncDate,DateUtil.DATE_FORMAT_YMDHM);
            quotaComputInsService.regularDataGather(syncCal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "syncInterfaceData 数据同步成功!";
    }

    /***
     * 重新初始化指标模板的表达式
     * @return
     */
    @ResponseBody
    @RequestMapping("quotaComput/reInitQuotaTemp.do")
    public String reInitQuotaTemp() {
        quotaComputInsService.init();
        return "重新更新指标模板成功!";
    }
}
