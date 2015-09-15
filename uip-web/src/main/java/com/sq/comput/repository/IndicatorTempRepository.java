package com.sq.comput.repository;

import com.sq.comput.domain.IndicatorTemp;
import com.sq.repository.BaseRepository;

/**
 * Created with IntelliJ IDEA.
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
public interface IndicatorTempRepository extends BaseRepository<IndicatorTemp,Long> {

    public IndicatorTemp findByIndicatorCode(String indicatorCode);
}
