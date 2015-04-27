package com.sq.comput.service;

import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 指标模板service.
 * User: shuiqing
 * Date: 2015/4/27
 * Time: 11:20
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class IndicatorTempService extends BaseService<IndicatorTemp,Long> {

    @Autowired
    @BaseComponent
    private IndicatorTempRepository indicatorTempRepository;
}
