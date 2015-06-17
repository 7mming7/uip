package com.sq.comput.controller;

import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.service.IndiComputService;
import com.sq.comput.service.IndicatorTempService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.util.DateUtil;
import com.sq.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * 指标计算控制器.
 * User: shuiqing
 * Date: 2015/6/17
 * Time: 18:48
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
@RequestMapping("/comput")
public class IndicatorComputController extends BaseController<IndicatorInstance,Long> {

    @Autowired
    @BaseComponent
    private IndiComputService indiComputService;

    @Autowired
    private IndicatorTempService indicatorTempService;

    @RequestMapping(value = "/reInterfaceComput.do", method = RequestMethod.GET)
    public void reInterfaceComput(@RequestParam("startCal") Integer startCal,@RequestParam("endCal") Integer endCal,@RequestParam("itemCode") String itemCode) {
        Calendar startCalendar = DateUtil.intDate2Calendar(startCal);
        Calendar endCalendar = DateUtil.intDate2Calendar(endCal);
        String[] itemArray = itemCode.split(",");
        List itemList = Arrays.asList(itemArray);
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorCode", MatchType.IN, itemList);
        List<IndicatorTemp> indicatorTempList = indicatorTempService.findAll(searchable).getContent();
        indiComputService.reComputInterface(startCalendar,endCalendar,indicatorTempList);
        System.out.println(startCal + "," + endCal + "," + itemCode);
    }
}
