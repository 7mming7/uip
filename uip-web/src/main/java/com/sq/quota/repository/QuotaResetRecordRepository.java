package com.sq.quota.repository;

import com.sq.quota.domain.QuotaResetRecord;
import com.sq.repository.BaseRepository;

import java.util.List;

/**
 * User: shuiqing
 * Date: 15/11/26
 * Time: 上午11:27
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public interface QuotaResetRecordRepository  extends BaseRepository<QuotaResetRecord, Long> {

    List<QuotaResetRecord> fetchResetRecord(String assQuotaCode, String assComputCal);
}
