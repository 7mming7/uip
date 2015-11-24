package com.sq.loadometer_nj.repository;

import com.sq.loadometer_nj.domain.LoadometerIndicatorDto;
import com.sq.loadometer_nj.domain.Trade;
import com.sq.repository.BaseRepository;

import java.util.List;

/**
 * 地磅数据仓库.
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 13:52
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface TradeDataRepository extends BaseRepository<Trade, Long> {

    /** 根据给定的日期查询，这个日期所产生的指标 */
    List<LoadometerIndicatorDto> queryForLoadometerIndicator(String queryDate);

    /** 根据二次称重的时间删除地磅流水同步表T_trade的数据 */
    void deleteDataBySecondTime(String secondTime);

    /** 根据指定的日期查询地磅的交易流水数据 */
    List<Trade> fetchTradeDataByPointDay(String pointDay);
}

