package com.sq.quota.repository;

import com.sq.quota.domain.QuotaTemp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * User: shuiqing
 * Date: 16/1/5
 * Time: 上午11:27
 * Email: annuus.sq@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class QuotaTempRepositoryImpl {

    private EntityManagerFactory emf;
    private EntityManager em;
    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<QuotaTemp> listQuotaTempByMp(){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append(" select DISTINCT it.* from t_indicatortemp it, t_mesuringpoint mp ")
                .append("where it.calculateExpression like CONCAT('%',mp.targetCode,'%') and mp.sysId != 2");
        Query query = em.createNativeQuery(nativeSql.toString(),QuotaTemp.class);

        List<QuotaTemp> quotaTempList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return quotaTempList;
    }
}