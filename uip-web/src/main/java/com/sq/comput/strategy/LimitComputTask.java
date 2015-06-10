package com.sq.comput.strategy;

import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.domain.LimitInstance;
import com.sq.comput.domain.LimitTemplate;
import com.sq.comput.repository.LimitTempRepository;
import com.sq.comput.service.LimitInstanceService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 限值计算线程，计算指标的限值..
 * User: shuiqing
 * Date: 2015/5/15
 * Time: 16:34
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class LimitComputTask implements Callable<List<LimitInstance>> {

    private Logger log = LoggerFactory.getLogger(LimitComputTask.class);

    /**
     * 由于Thread非spring启动时实例化，而是根据具体的逻辑动态实例化，所以需要通过此方式从spring的context中获取相应的bean.
     */
    private LimitTempRepository limitTempRepository = SpringUtils.getBean(LimitTempRepository.class);

    private LimitInstanceService limitInstanceService = SpringUtils.getBean(LimitInstanceService.class);

    private IndicatorTemp indicatorTemp;

    private Calendar computCal;

    public LimitComputTask(IndicatorTemp indicatorTemp, Calendar computCal) {
        this.indicatorTemp = indicatorTemp;
        this.computCal = computCal;
    }

    public IndicatorTemp getIndicatorTemp() {
        return indicatorTemp;
    }

    public void setIndicatorTemp(IndicatorTemp indicatorTemp) {
        this.indicatorTemp = indicatorTemp;
    }

    public Calendar getComputCal() {
        return computCal;
    }

    public void setComputCal(Calendar computCal) {
        this.computCal = computCal;
    }

    @Override
    public List<LimitInstance> call() throws Exception {
        List<LimitInstance> limitInstanceList = new LinkedList<LimitInstance>();
        Searchable searchable =  Searchable.newSearchable()
                .addSearchFilter("indicatorTemp.id",MatchType.EQ, indicatorTemp.getId());
        List<LimitTemplate> limitTemplateList = limitTempRepository.findAll(searchable).getContent();
        for (LimitTemplate limitTemplate:limitTemplateList) {
            limitInstanceList.add(limitInstanceService.execLimitComput(limitTemplate,computCal));
            this.limitInstanceService.save(limitInstanceService.execLimitComput(limitTemplate,computCal));
        }
        return limitInstanceList;
    }
}