package com.sq.protocol.opc.repository;

import com.sq.protocol.opc.domain.OriginalData;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
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

    private EntityManagerFactory emf;
    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dcsDataMigration(final String calculateDay) {
        EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("call data_migration_indicator(" + calculateDay + ") ").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void njmbDataSync() {
        EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("call njmb_every5min_flush() ").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OriginalData> listAnHourPreOriginalData(final String indiCode){
        EntityManager em = this.emf.createEntityManager();
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
