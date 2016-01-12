package com.sq.quota.repository;

import com.sq.quota.domain.QuotaInstance;
import com.sq.repository.BaseRepository;

import java.util.List;

/**
 * 指标计算结果实例仓库.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
        */
public interface QuotaInstanceRepository extends BaseRepository<QuotaInstance, Long> {

    //根据某个时间点去查询指标实例数据
    List<QuotaInstance> listQuotaInstanceInstTime(String assQuotaCode, String assComputCal);
}