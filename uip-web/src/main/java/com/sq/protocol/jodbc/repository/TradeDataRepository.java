package com.sq.protocol.jodbc.repository;

import com.sq.comput.domain.IndicatorInstance;
import com.sq.protocol.jodbc.domain.Threshold;
import com.sq.protocol.jodbc.domain.Trade;
import com.sq.repository.BaseRepository;

import java.util.Calendar;
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

    /** 增量同步地磅流水数据 */
    public List<IndicatorInstance> genTradeIndicators(Calendar[] calArray);

    /** 查询最后同步记录值 */
    public Threshold maxThreshold();
}

