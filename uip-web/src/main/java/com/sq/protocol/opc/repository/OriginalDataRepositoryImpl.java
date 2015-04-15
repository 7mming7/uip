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
}
