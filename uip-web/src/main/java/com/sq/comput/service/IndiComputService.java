package com.sq.comput.service;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标计算业务类.
 * User: shuiqing
 * Date: 2015/4/17
 * Time: 11:02
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class IndiComputService extends BaseService<IndicatorInstance,Long>{

    private static Logger log = LoggerFactory.getLogger(IndiComputService.class);

    @Autowired
    @BaseComponent
    private IndicatorInstanceRepository indicatorInstanceRepository;

    @Autowired
    private IndicatorTempRepository indicatorTempRepository;

    /**
     * 接口数据汇集到系统的最小维度小时级
     */
    public void interfaceDataGather () {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("dataSource", IndicatorConsts.DATASOURCE_INTERFACE);
        Searchable searchable = Searchable.newSearchable(searchParams);
        Page<IndicatorTemp> indicatorTempPage = this.indicatorTempRepository.findAll(searchable);
        List<IndicatorTemp> indicatorTempList = indicatorTempPage.getContent();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            System.out.println("indicatorTemp:->" + indicatorTemp.getIndicatorName());
        }
    }
}
