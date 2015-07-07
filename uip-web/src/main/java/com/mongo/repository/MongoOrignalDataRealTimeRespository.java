package com.mongo.repository;

import com.mongo.base.BaseMongoRepository;
import com.mongo.domain.MongoOriginalDataHistory;
import com.mongo.domain.MongoOrignalDataRealTime;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/6/30
 * Time: 17:11
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface MongoOrignalDataRealTimeRespository extends BaseMongoRepository<MongoOrignalDataRealTime,Long> {

    /** 根据SYSID查询实时数据 */
    List<MongoOrignalDataRealTime> findBySysId(int sysId);
}
