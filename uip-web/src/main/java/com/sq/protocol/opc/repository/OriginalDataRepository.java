package com.sq.protocol.opc.repository;

import com.sq.protocol.opc.domain.OriginalData;
import com.sq.repository.BaseRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;

/**
 * 测点同步结果仓库.
 * User: shuiqing
 * Date: 2015/4/6
 * Time: 19:18
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface OriginalDataRepository extends BaseRepository<OriginalData, Long> {

    /** DCS原始测点实时数据迁移 */
    void dcsDataMigration(String calculateDay);

    /** 大屏数据更新同步 */
    void njmbDataSync();

    /** 获取下一数据获取DCS批次 */
    @Query("select 1 + IFNULL(max(batchNum),0) from OriginalData where sysId = ?1")
    Long gernateNextBatchNumber(int sysId);

    /** 查询指标测点指定时间内的数据 */
    List<OriginalData> listAnHourPreOriginalData(String tableName, String indiCode, Long subMin, Calendar computCal);
}
