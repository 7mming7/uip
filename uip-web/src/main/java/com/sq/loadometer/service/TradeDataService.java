package com.sq.loadometer.service;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.loadometer.component.DblinkConnecter;
import com.sq.loadometer.component.JdbcHelper;
import com.sq.loadometer.domain.LoadometerIndicatorDto;
import com.sq.loadometer.domain.Trade;
import com.sq.loadometer.repository.TradeDataRepository;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 地磅业务类--负责数据同步和指标生成
 * User: shuiqing
 * Date: 2015/9/15
 * Time: 10:20
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class TradeDataService extends BaseService<Trade, Long> {

    private static final Logger log = LoggerFactory.getLogger(TradeDataService.class);

    @Autowired
    @BaseComponent
    private TradeDataRepository tradeDataRepository;

    @Autowired
    private IndicatorTempRepository indicatorTempRepository;

    @Autowired
    private IndicatorInstanceRepository indicatorInstanceRepository;

    /**
     * 地磅流水数据同步
     */
    public void syncLoadometerTrade (String syncCal) {
        removeCurrDayTradeData(syncCal);
        insertCurrDayTradeData(syncCal);
        generateLoadometerIndicator(syncCal);
    }

    /**
     * 清除当日的已同步的流水数据
     * @param removeTradeDay 删除日期
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeCurrDayTradeData (String removeTradeDay) {
        tradeDataRepository.deleteDataBySecondTime(removeTradeDay);
    }

    /**
     * 填充当日的流水数据
     * @param fillTradeData 填充日期
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertCurrDayTradeData (String fillTradeData) {
        List<Trade> tradeList = new ArrayList<Trade>();

        StringBuilder insertTradeBuilder = new StringBuilder();
        insertTradeBuilder
                .append(" select ")
                .append("       t.lsh AS lsh,  ")
                .append("       t.ch AS carNo, ")
                .append("       t.hm AS proCode, ")
                .append("       t.dwdw AS sourceArea, ")
                .append("       t.cmrs AS firstWeightTime, ")
                .append("       t.cprs AS secondWeightTime, ")
                .append("       t.mz AS gross, ")
                .append("       t.pz AS tare, ")
                .append("       t.jz AS net, ")
                .append("       t.czy AS operator, ")
                .append("       CONVERT (VARCHAR(12), t.cprs, 112) AS statDateNum ")
                .append("    FROM   ")
                .append("       czb t ")
                .append("    WHERE   ")
                .append("       CONVERT (VARCHAR(12), t.cprs, 112) =  ")
                .append(fillTradeData);
        try {
            List<HashMap<String,String>> resultList = JdbcHelper.query(insertTradeBuilder.toString());
            for (HashMap tradeMap:resultList) {
                Trade trade = new Trade(tradeMap);
                Double gross = Double.parseDouble(trade.getGross())/DblinkConnecter.load_ratio;
                trade.setGross(gross.toString());

                Double tare = Double.parseDouble(trade.getTare())/DblinkConnecter.load_ratio;
                trade.setTare(tare.toString());

                Double net = Double.parseDouble(trade.getNet())/DblinkConnecter.load_ratio;
                trade.setNet(net.toString());
                tradeList.add(trade);
            }
        } catch (SQLException e) {
            log.error("执行query error：" + insertTradeBuilder.toString());
        }
        tradeDataRepository.save(tradeList);
    }

    /**
     * 生成日地磅指标
     * @param generateDate 生成日期
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void generateLoadometerIndicator (String generateDate) {
        List<IndicatorInstance> indicatorInstanceList = new ArrayList<IndicatorInstance>();

        //查询地磅指标数据
        List<LoadometerIndicatorDto> loadometerIndicatorDtoList = tradeDataRepository.queryForLoadometerIndicator(generateDate);
        List<String> loadometerCodeList = new ArrayList<String>();
        for (LoadometerIndicatorDto loadometerIndicatorDto:loadometerIndicatorDtoList) {
            loadometerCodeList.add(loadometerIndicatorDto.getIndicatorCode());
        }

        if (!loadometerCodeList.isEmpty()) {
            //删除已经存在的当日的地磅指标数据
            Searchable removeLoadometerCodeSearchable = Searchable.newSearchable()
                    .addSearchFilter("indicatorCode", MatchType.IN, loadometerCodeList)
                    .addSearchFilter("statDateNum", MatchType.EQ, generateDate);
            indicatorInstanceRepository.deleteInBatch(indicatorInstanceRepository.findAll(removeLoadometerCodeSearchable));
        }

        //保存查询到的当日地磅指标数据
        for(LoadometerIndicatorDto loadometerIndicatorDto:loadometerIndicatorDtoList) {
            IndicatorTemp indicatorTemp = indicatorTempRepository.findByIndicatorCode(loadometerIndicatorDto.getIndicatorCode());
            IndicatorInstance indicatorInstance = new IndicatorInstance(indicatorTemp);
            try {
                indicatorInstance.setFloatValue(Double.parseDouble(loadometerIndicatorDto.getTotalAmount()));
                indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_DOUBLE);
                indicatorInstance.setStatDateNum(Integer.parseInt(generateDate));
                indicatorInstance.setInstanceTime(DateUtil.stringToCalendar(generateDate,DateUtil.DATE_FORMAT_DAFAULT));
            } catch (ParseException e) {
                log.error("stringToCalendar error:", e);
            }
            indicatorInstanceList.add(indicatorInstance);
        }
        indicatorInstanceRepository.save(indicatorInstanceList);
    }
}
