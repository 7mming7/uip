package com.sq.protocol.opc.repository;

import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 测点仓库.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:33
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public interface MesuringPointRepository extends BaseRepository<MesuringPoint, Long> {

    /** 根据编码查询测点 */
    @Query("select m from MesuringPoint m where sourceCode = ?1")
    MesuringPoint fetchMpByCode(String postCode);
}
