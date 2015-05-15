package com.sq.comput.service;

import com.sq.comput.domain.LimitInstance;
import com.sq.comput.repository.LimitInstanceRepository;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/5/15
 * Time: 16:39
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class LimitInstanceService extends BaseService<LimitInstance,Integer> {

    @Autowired
    @BaseComponent
    private LimitInstanceRepository limitInstanceRepository;
}