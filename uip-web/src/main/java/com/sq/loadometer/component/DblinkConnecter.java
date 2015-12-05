package com.sq.loadometer.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * ODBC-JDBC连接器.
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
public class DblinkConnecter {

    private static final Logger log = LoggerFactory.getLogger(DblinkConnecter.class);

    /** 配置文件路径 */
    private static Properties prop;

    private static String ODBC_CONFIG_URL = "odbc_url";

    private static String ODBC_CONFIG_USERNAME = "odbc_username";

    private static String ODBC_CONFIG_PASSWORD = "odbc_password";

    private static String ODBC_CONFIG_RATIO = "odbc_ratio";

    /** 地磅数据转换倍率 */
    public static int load_ratio;

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/odbc-jdbc-config.properties";

    static {
        loadConfigProperties();
    }

    public static Connection connSqlserver () {
        Connection connection = null;
        try {
            log.info("ODBC->开始链接。。。。。。。。");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(getEntryValue(ODBC_CONFIG_URL),
                    getEntryValue(ODBC_CONFIG_USERNAME),
                    getEntryValue(ODBC_CONFIG_PASSWORD));
        } catch (Exception e) {
            log.error("ODBC连接失败.", e);
        }
        return connection;
    }

    public static Properties loadConfigProperties (){
        prop = new Properties();
        try {
            prop.load(DblinkConnecter.class.getResourceAsStream(CONFIG_FILE_NAME));
            load_ratio = Integer.parseInt(getEntryValue(ODBC_CONFIG_RATIO));
        } catch (IOException e){
            log.error("ODBC-JDBC 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    public static String getEntryValue(String name) {
        return prop.getProperty(name);
    }

    /**
     * 释放连接
     * @param conn
     */
    private static void freeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("freeConnection error!",e);
        }
    }

    /**
     * 释放statement
     * @param statement
     */
    private static void freeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            log.error("freeStatement error!" ,e);
        }
    }

    /**
     * 释放resultset
     * @param rs
     */
    private static void freeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            log.error("freeResultSet error!",e);
        }
    }

    /**
     * 释放资源
     *
     * @param conn
     * @param statement
     * @param rs
     */
    public static void free(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            freeResultSet(rs);
        }
        if (statement != null) {
            freeStatement(statement);
        }
        if (conn != null) {
            freeConnection(conn);
        }
    }
}
