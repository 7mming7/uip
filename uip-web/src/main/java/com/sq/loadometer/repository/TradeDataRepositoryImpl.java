package com.sq.loadometer.repository;

import com.sq.loadometer.domain.LoadometerConsts;
import com.sq.loadometer.domain.LoadometerIndicatorDto;
import com.sq.util.NativeQueryResultsMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    private EntityManager em;

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public List<LoadometerIndicatorDto> queryForLoadometerIndicator(String queryDate){
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append(" SELECT ")
                .append("      MP.sourceCode as sourceCode, ")
                .append("      MP.targetCode as indicatorCode, ")
                .append("      IFNULL(SUM(T.productnet),0) as totalAmount ")
                .append("  FROM ")
                .append("      t_indicatortemp IT LEFT JOIN ")
                .append("      t_mesuringpoint MP on IT.indicatorCode = MP.targetCode LEFT JOIN ")
                .append("      t_trade T ")
                .append("   ON MP.sourceCode = T.productcode  ")
                .append("      AND DATE_FORMAT(T.seconddatetime, '%Y%m%d') = ").append(queryDate)
                .append("   WHERE MP.sysId =  ").append(LoadometerConsts.SYS_ODBC_LOADOMETER)
                .append("    GROUP BY MP.sourceCode ");
        Query query = em.createNativeQuery(nativeSql.toString());
        return NativeQueryResultsMapper.map(query.getResultList(), LoadometerIndicatorDto.class);
    }

    /**
     * 删除某一日的地磅流水同步表中的流水数据
     * @param secondTime 二次称重时间
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteDataBySecondTime (String secondTime) {
        StringBuilder nativeSql = new StringBuilder();
        em.getTransaction().begin();
        nativeSql.append("delete from t_trade where DATE_FORMAT(seconddatetime,'%Y%m%d') = ").append(secondTime);
        Query query = em.createNativeQuery(nativeSql.toString());
        query.executeUpdate();
        em.getTransaction().commit();
    }
}
