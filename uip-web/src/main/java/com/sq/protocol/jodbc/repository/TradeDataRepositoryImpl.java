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

    public void genTradeIndicators(Calendar[] calArray) throws Exception{
        EntityManager em = this.emf.createEntityManager();
        StringBuilder nativeSql = new StringBuilder();
        nativeSql.append("INSERT INTO t_indicatorinstancecurrent (")
                 .append("    dataSource,decimalNum,description, ")
                 .append("    fetchCycle,indicatorCode,indicatorName, ")
                 .append("    unit,categoryId,floatValue, ")
                 .append("    indicatorTempId,valueType,calType, ")
                 .append("    operCalType,createTime,instanceTime,statDateNum) ")
                 .append("SELECT ")
                 .append("    a.dataSource,a.decimalNum,a.description, ")
                 .append("    a.fetchCycle,a.indicatorCode,a.indicatorName, ")
                 .append("    a.unit,a.categoryId,IFNULL(b.countPrd, 0) floatValue, ")
                 .append("    a.indicatorTempId,2 valueType, ")
                 .append("    a.calType,a.operCalType,NOW() creatTime, ")
                 .append("    ?1 instanceTime, date_format(?2,'%Y%m%d') statDateNum ")
                 .append("  FROM( ")
                 .append("    SELECT ")
                 .append("        obj.dataSource dataSource,obj.decimalNum,obj.description, ")
                 .append("        obj.fetchCycle,obj.indicatorCode,obj.indicatorName, ")
                 .append("        obj.unit,obj.categoryId,obj.id indicatorTempId, ")
                 .append("        obj.calType,obj.operCalType,mp.sourceCode, ")
                 .append("        mp.targetCode ")
                 .append("    FROM ")
                 .append("        t_indicatortemp obj, ")
                 .append("        T_MesuringPoint mp ")
                 .append("    WHERE ")
                 .append("        mp.sysId = ?3 ")
                 .append("        AND obj.indicatorCode = mp.targetCode) a ")
                 .append("    LEFT JOIN ( ")
                 .append("        SELECT ")
                 .append("            tra.productcode prdCode, ")
                 .append("            sum(tra.productnet) countPrd ")
                 .append("        FROM ")
                 .append("            t_trade tra ")
                 .append("        WHERE ")
                 .append("            tra.seconddatetime BETWEEN ?4 AND ?5 ")
                 .append("        GROUP BY tra.productcode ")
                 .append("        ) b ON a.sourceCode = b.prdCode ")
                 .append("     GROUP BY a.targetCode ");
        em.getTransaction().begin();
        Query query = em.createNativeQuery(nativeSql.toString(),IndicatorInstance.class);
        query.setParameter(1, calArray[0]);
        query.setParameter(2, calArray[0]);
        query.setParameter(3, JodbcConsts.SYS_ODBC_LOADOMETER);
        query.setParameter(4, calArray[0]);
        query.setParameter(5, calArray[1]);
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
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