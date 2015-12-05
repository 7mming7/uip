package com.sq.loadometer.repository;

import com.sq.loadometer.domain.LoadometerConsts;
import com.sq.loadometer.domain.LoadometerIndicatorDto;
import com.sq.loadometer.domain.Trade;
import com.sq.util.NativeQueryResultsMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * 地磅仓库IMPL
 * User: shuiqing
 * Date: 15/11/24
 * Time: 上午11:25
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
        StringBuilder nativeSql = new StringBuilder();
        EntityManager em = emf.createEntityManager();
        nativeSql.append(" SELECT ")
                .append("      MP.sourceCode as sourceCode, ")
                .append("      MP.targetCode as indicatorCode, ")
                .append("      IFNULL(SUM(T.net),0) as totalAmount ")
                .append("  FROM ")
                .append("      t_indicatortemp IT LEFT JOIN ")
                .append("      t_mesuringpoint MP on IT.indicatorCode = MP.targetCode LEFT JOIN ")
                .append("      t_trade T ")
                .append("   ON MP.sourceCode = T.proCode  ")
                .append("      AND DATE_FORMAT(T.secondWeightTime, '%Y%m%d') = ").append(queryDate)
                .append("   WHERE MP.sysId =  ").append(LoadometerConsts.SYS_ODBC_LOADOMETER)
                .append("    GROUP BY MP.sourceCode ");
        Query query = em.createNativeQuery(nativeSql.toString());
        return NativeQueryResultsMapper.map(query.getResultList(), LoadometerIndicatorDto.class);
    }

    /**
     * 删除某一日的地磅流水同步表中的流水数据
     * @param secondTime 二次称重时间
     */
    public void deleteDataBySecondTime (String secondTime) {
        StringBuilder nativeSql = new StringBuilder();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        nativeSql.append("delete from t_trade where DATE_FORMAT(secondWeightTime,'%Y%m%d') = ").append(secondTime);
        Query query = em.createNativeQuery(nativeSql.toString());
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public List<Trade> fetchTradeDataByPointDay (String pointDay) {
        StringBuilder nativeSql = new StringBuilder();
        EntityManager em = emf.createEntityManager();
        nativeSql.append("select * from t_trade t where DATE_FORMAT(t.seconddatetime,'%Y%m%d') = ?1");
        Query query = em.createNativeQuery(nativeSql.toString(),Trade.class);
        query.setParameter(1, pointDay);

        List<Trade> tradeList = query.getResultList();
        return tradeList;
    }
}
