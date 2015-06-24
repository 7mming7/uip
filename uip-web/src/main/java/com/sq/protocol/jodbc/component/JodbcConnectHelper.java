package com.sq.protocol.jodbc.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * ODBC连接辅助类.
 * User: shuiqing
 * Date: 2015/5/12
 * Time: 10:51
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class JodbcConnectHelper {

    private static final Logger log = LoggerFactory.getLogger(JodbcConnectHelper.class);

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/odbc-jdbc-config.properties";

    private static Properties prop;

    /**
     * create a connection.
     * @param dbType 数据库类型
     * @return 连接实例
     */
    public static Connection connect(int dbType) {
        DblinkConfig configuration = new DblinkConfig();
        configuration.loadConfigProperties(CONFIG_FILE_NAME);
        Connection connection = configuration.openCon(dbType);
        return connection;
    }

    /**
     * release a connection.
     * @param connection  已经创建的实例
     */
    public static void releaseConn(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Odbc. release连接出现异常.", e);
        }
    }
}
