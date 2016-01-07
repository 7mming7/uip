package com.sq.quota.repository;

import com.sq.quota.domain.QuotaTemp;
import com.sq.repository.BaseRepository;

import java.util.List;

/**
 * 指标模板仓库.
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
public interface QuotaTempRepository extends BaseRepository<QuotaTemp,Long> {

    QuotaTemp findByIndicatorCode(String indicatorCode);

    /** 根据测点的系统编码查询，该子系统的直属指标模板 */
    List<QuotaTemp> listQuotaTempByMp();
}
