package com.sq.quota.repository;

import com.sq.quota.domain.QuotaInstance;
import com.sq.quota.domain.QuotaResetRecord;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * User: shuiqing
 * Date: 16/1/5
 * Time: 下午2:36
 * Email: annuus.sq@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class QuotaInstanceRepositoryImpl {

    private EntityManagerFactory emf;
    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<QuotaInstance> listQuotaInstanceInstTime(final String assQuotaCode, final String assComputCal) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append("select t.* from t_indicatorinstancecurrent t ")
                .append("     where t.indicatorCode = ?1 and DATE_FORMAT(t.instanceTime,'%Y%m%d%H%i') = ?2 ");
        Query query = em.createNativeQuery(nativeSql.toString(),QuotaInstance.class);
        query.setParameter(1, assQuotaCode);
        query.setParameter(2, assComputCal);
        List<QuotaInstance> quotaInstanceList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return quotaInstanceList;
    }
}
