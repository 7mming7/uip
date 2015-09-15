package com.sq.loadometer.repository;

import com.sq.loadometer.domain.LoadometerConsts;
import com.sq.loadometer.domain.LoadometerIndicatorDto;
import com.sq.protocol.jodbc.domain.Threshold;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * User: shuiqing
 * Date: 2015/9/15
 * Time: 15:59
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

    public List<LoadometerIndicatorDto> queryForLoadometerIndicator(String queryDate){
        EntityManager em = this.emf.createEntityManager();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append(" SELECT ")
                .append("      MP.sourceCode, ")
                .append("      MP.targetCode as indicatorCode, ")
                .append("      IFNULL(SUM(T.productnet),0) totalAmount ")
                .append("  FROM ")
                .append("      t_indicatortemp IT LEFT JOIN ")
                .append("      t_mesuringpoint MP on IT.indicatorCode = MP.targetCode LEFT JOIN ")
                .append("      t_trade T ")
                .append("   ON MP.sourceCode = T.productcode  ")
                .append("      AND DATE_FORMAT(T.seconddatetime, '%Y%m%d') = ").append(queryDate)
                .append("   WHERE MP.sysId =  ").append(LoadometerConsts.SYS_ODBC_LOADOMETER)
                .append("    GROUP BY MP.sourceCode ");
        Query query = em.createNativeQuery(nativeSql.toString(), LoadometerIndicatorDto.class);
        return query.getResultList();
    }
}
