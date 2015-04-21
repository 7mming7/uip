package com.sq.protocol.opc.repository;

import com.sq.protocol.opc.domain.OriginalData;
import com.sq.repository.support.SimpleBaseRepository;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 12:00
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class OriginalDataRepositoryImpl{

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED)
    public void dcsDataMigration(final String calculateDay) {
        em.createNativeQuery("call data_migration_indicator(" + calculateDay + ") ").executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OriginalData> listAnHourPreOriginalData(final String indiCode){
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append(" SELECT ")
                 .append("       icm.targetCode AS targetCode, ")
                 .append("       od.batchNum AS batchNum, ")
                 .append("       od.instanceTime AS instanceTime, ")
                 .append("       od.itemValue AS itemValue, ")
                 .append("       od.sysId AS sysId ")
                 .append("  FROM   ")
                 .append("       t_mesuringpoint icm, ")
                 .append("       t_originaldata od ")
                 .append("  WHERE ")
                 .append("       icm.sourceCode = od.itemCode ")
                 .append("   AND icm.targetCode = ?1 ")
                 .append("   AND od.instanceTime BETWEEN date_sub(now(), INTERVAL 1 HOUR) ")
                 .append("              AND NOW() ")
                 .append("  ORDER BY od.instanceTime ASC ");
        Query query = em.createNativeQuery(nativeSql.toString(),OriginalData.class);
        query.setParameter(1,indiCode);

        List<OriginalData> originalDataList = query.getResultList();

        return originalDataList;
    }
}
