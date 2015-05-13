package com.sq.protocol.odbc.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * ODBC配置对象.
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
public class OdbcConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OdbcConfiguration.class);

    /** 配置文件路径 */
    private static Properties prop;

    private static String ODBC_CONFIG_URL = "url";

    private static String ODBC_CONFIG_USERNAME = "username";

    private static String ODBC_CONFIG_PASSWORD = "password";

    public static Connection openCon (){
        Connection connection = null;
        try {
            log.error("ODBC开始链接。。。。。。。。");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(getEntryValue(ODBC_CONFIG_URL), getEntryValue(ODBC_CONFIG_USERNAME), getEntryValue(ODBC_CONFIG_PASSWORD));
        } catch (Exception e) {
            log.error("ODBC连接失败.", e);
        }
        return connection;
    }

    public Properties loadConfigProperties (String CONFIG_FILE_NAME){
        prop = new Properties();
        try {
            prop.load(OdbcConfiguration.class.getResourceAsStream(CONFIG_FILE_NAME));

        } catch (IOException e){
            log.error("ODBC 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }
    /**
     * 通过名字获得配置的值
     *
     * @param name
     * @return
     */
    public static String getEntryValue(String name) {
        return prop.getProperty(name);
    }
}
