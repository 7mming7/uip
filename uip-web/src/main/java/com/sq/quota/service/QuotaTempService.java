package com.sq.quota.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.repository.QuotaTempRepository;
import com.sq.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/8/31
 * Time: 17:18
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class QuotaTempService extends BaseService<QuotaTemp,Long> {

    @Autowired
    @BaseComponent
    private QuotaTempRepository quotaTempRepository;
}
