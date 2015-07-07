package com.sq.protocol.jodbc.repository;

import com.sq.comput.domain.IndicatorInstance;
import com.sq.protocol.jodbc.domain.JodbcConsts;
import com.sq.protocol.jodbc.domain.Threshold;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 13:55
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TradeDataRepositoryImpl {

    private EntityManagerFactory emf;

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Threshold maxThreshold(){
        EntityManager em = this.emf.createEntityManager();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append("select * from T_Threshold where id = (select max(id) from T_Threshold )");
        Query query = em.createNativeQuery(nativeSql.toString(),Threshold.class);
        Threshold threshold = (Threshold)query.getSingleResult();
        return threshold;
    }
}