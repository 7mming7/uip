package com.sq.quota.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.quota.domain.QuotaResetRecord;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.repository.QuotaResetRecordRepository;
import com.sq.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 指标重置服务
 * User: shuiqing
 * Date: 15/11/26
 * Time: 上午11:30
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class QuotaResetService extends BaseService<QuotaResetRecord,Long> {

    @Autowired
    @BaseComponent
    private QuotaResetRecordRepository quotaResetRecordRepository;
}
