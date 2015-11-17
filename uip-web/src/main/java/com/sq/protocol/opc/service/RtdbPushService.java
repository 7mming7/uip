package com.sq.protocol.opc.service;

import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午2:05
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class RtdbPushService{

    private static final Logger log = LoggerFactory.getLogger(RtdbPushService.class);

    @Autowired
    @BaseComponent
    private OriginalDataRepository originalDataRepository;

    @Autowired
    private MesuringPointRepository mesuringPointRepository;

    /**
     * 获取指定测点的实时数据
     * @param mesuringPointList 需要获取实时数据的编码的集合
     * @return 获取的实时数据
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public List<OriginalData> fetchOriDataList(List<MesuringPoint> mesuringPointList){
        if (mesuringPointList.isEmpty()) {
            return null;
        }

        List<String> itemCodeList = new ArrayList<String>();
        for (MesuringPoint mesuringPoint:mesuringPointList) {
            itemCodeList.add(mesuringPoint.getSourceCode());
        }

        List<OriginalData> originalDataList = originalDataRepository.fetchOriDataByCodeList(itemCodeList);

        return originalDataList;
    }

    /**
     * 根据请求的测点编码查询MP测点
     * @param postCode 传入的测点的编码
     * @return 测点
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public MesuringPoint fetchMpByPostCode(String postCode){
        return mesuringPointRepository.fetchMpByCode(postCode);
    }
}
