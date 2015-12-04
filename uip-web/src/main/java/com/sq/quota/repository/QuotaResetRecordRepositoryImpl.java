package com.sq.quota.repository;

import com.sq.protocol.opc.domain.OriginalData;
import com.sq.quota.domain.QuotaResetRecord;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * User: shuiqing
 * Date: 15/12/1
 * Time: 下午12:18
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class QuotaResetRecordRepositoryImpl {

    private EntityManagerFactory emf;
    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<QuotaResetRecord> fetchResetRecord(final String assQuotaCode, final String assComputCal){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append(" select * from t_indicatorresetdata ir,t_indicatortemp it " +
                "where ir.indicatorTempId = it.id and it.indicatorCode = ?1 " +
                "and DATE_FORMAT(ir.resetDate,'%Y%m%d%H%i%s') = ?2");
        Query query = em.createNativeQuery(nativeSql.toString(),QuotaResetRecord.class);
        query.setParameter(1, assQuotaCode);
        query.setParameter(2, assComputCal);

        List<QuotaResetRecord> quotaResetRecordList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return quotaResetRecordList;
    }
}
