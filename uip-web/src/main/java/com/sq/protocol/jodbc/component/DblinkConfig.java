package com.sq.protocol.jodbc.component;

import com.sq.protocol.jodbc.domain.JodbcConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * ODBC-JDBC配置对象.
 * User: shuiqing
 * Date: 2015/5/12
 * Time: 10:18
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class DblinkConfig {

    private static final Logger log = LoggerFactory.getLogger(DblinkConfig.class);

    /** 配置文件路径 */
    private static Properties prop;

    private static String ODBC_CONFIG_URL = "odbc_url";

    private static String ODBC_CONFIG_USERNAME = "odbc_username";

    private static String ODBC_CONFIG_PASSWORD = "odbc_password";

    private static String JDBC_CONFIG_URL = "jdbc_url";

    private static String JDBC_CONFIG_USERNAME = "jdbc_username";

    private static String JDBC_CONFIG_PASSWORD = "jdbc_password";

    public static Connection openCon (int dbType){
        Connection connection = null;
        switch (dbType) {
            case JodbcConsts.DB_SQLSERVER:
                connection = connSqlserver();
                break;
            case JodbcConsts.DB_MYSQL:
                connection = connMysql();
                break;
        }
        return connection;
    }

    public static Connection connSqlserver () {
        Connection connection = null;
        try {
            log.info("ODBC->开始链接。。。。。。。。");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(getEntryValue(ODBC_CONFIG_URL), getEntryValue(ODBC_CONFIG_USERNAME), getEntryValue(ODBC_CONFIG_PASSWORD));
        } catch (Exception e) {
            log.error("ODBC连接失败.", e);
        }
        return connection;
    }

    public static Connection connMysql () {
        Connection connection = null;
        try {
            log.info("JDBC->开始链接。。。。。。。。");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(getEntryValue(JDBC_CONFIG_URL), getEntryValue(JDBC_CONFIG_USERNAME), getEntryValue(JDBC_CONFIG_PASSWORD));
        } catch (Exception e) {
            log.error("JDBC连接失败.", e);
        }
        return connection;
    }

    public Properties loadConfigProperties (String CONFIG_FILE_NAME){
        prop = new Properties();
        try {
            prop.load(DblinkConfig.class.getResourceAsStream(CONFIG_FILE_NAME));
        } catch (IOException e){
            log.error("ODBC-JDBC 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    public static String getEntryValue(String name) {
        return prop.getProperty(name);
    }
}
