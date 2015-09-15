package com.sq.loadometer.service;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.comput.service.IndicatorTempService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.loadometer.component.JdbcHelper;
import com.sq.loadometer.domain.LoadometerConsts;
import com.sq.loadometer.domain.LoadometerIndicatorDto;
import com.sq.loadometer.domain.Trade;
import com.sq.loadometer.repository.TradeDataRepository;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
    public void syncLoadometerTrade (Calendar syncCal) {
        String syncTradeDay = DateUtil.formatCalendar(syncCal, DateUtil.DATE_FORMAT_DAFAULT);
        removeCurrDayTradeData(syncTradeDay);
        insertCurrDayTradeData(syncTradeDay);
        generateLoadometerIndicator(syncTradeDay);
    }

    /**
     * 清除当日的已同步的流水数据
     * @param removeTradeDay 删除日期
     */
    public void removeCurrDayTradeData (String removeTradeDay) {
        Searchable removeSearchable= Searchable.newSearchable()
                .addSearchFilter("DATE_FORMAT(seconddatetime,'%Y%m%d')", MatchType.EQ, removeTradeDay);
        List<Trade> tradeList = tradeDataRepository.findAll(removeSearchable).getContent();
        tradeDataRepository.delete(tradeList);
    }

    /**
     * 填充当日的流水数据
     * @param fillTradeData 填充日期
     */
    public void insertCurrDayTradeData (String fillTradeData) {
        List<Trade> tradeList = new ArrayList<Trade>();

        StringBuilder insertTradeBuilder = new StringBuilder();
        insertTradeBuilder
                .append(" select * from Trade where productNet is not null and DATE_FORMAT(seconddatetime,'%Y%m%d') = ")
                .append(fillTradeData);
        try {
            tradeList = JdbcHelper.query(insertTradeBuilder.toString());
        } catch (SQLException e) {
            log.error("执行query error：" + insertTradeBuilder.toString());
        }
        tradeDataRepository.save(tradeList);
    }

    /**
     * 生成日地磅指标
     * @param generateDate 生成日期
     */
    public void generateLoadometerIndicator (String generateDate) {
        List<IndicatorInstance> indicatorInstanceList = new ArrayList<IndicatorInstance>();

        //查询地磅指标数据
        List<LoadometerIndicatorDto> loadometerIndicatorDtoList = tradeDataRepository.queryForLoadometerIndicator(generateDate);
        List<String> loadometerCodeList = new ArrayList<String>();
        for (LoadometerIndicatorDto loadometerIndicatorDto:loadometerIndicatorDtoList) {
            loadometerCodeList.add(loadometerIndicatorDto.getIndicatorCode());
        }

        //删除已经存在的当日的地磅指标数据
        Searchable removeLoadometerCodeSearchable = Searchable.newSearchable()
                .addSearchFilter("indicatorCode", MatchType.IN, loadometerCodeList)
                .addSearchFilter("statDateNum", MatchType.EQ, generateDate);
        indicatorInstanceRepository.deleteInBatch(indicatorInstanceRepository.findAll(removeLoadometerCodeSearchable));

        //保存查询到的当日地磅指标数据
        for(LoadometerIndicatorDto loadometerIndicatorDto:loadometerIndicatorDtoList) {
            IndicatorTemp indicatorTemp = indicatorTempRepository.findByIndicatorCode(loadometerIndicatorDto.getIndicatorCode());
            IndicatorInstance indicatorInstance = new IndicatorInstance(indicatorTemp);
            indicatorInstance.setFloatValue(Double.parseDouble(loadometerIndicatorDto.getTotalAmount()));
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_DOUBLE);
            indicatorInstance.setStatDateNum(Integer.parseInt(DateUtil.DATE_FORMAT_DAFAULT));
            indicatorInstance.setInstanceTime(Calendar.getInstance());
            indicatorInstanceList.add(indicatorInstance);
        }
        indicatorInstanceRepository.save(indicatorInstanceList);
    }
}
