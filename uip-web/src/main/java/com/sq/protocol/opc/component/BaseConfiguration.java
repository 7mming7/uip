package com.sq.protocol.opc.component;

import org.openscada.opc.lib.common.ConnectionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Opc基础配置，主要是连接的一些配置属性.
 * User: shuiqing
 * Date: 2015/3/30
 * Time: 9:40
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class BaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BaseConfiguration.class);

    private final static ConnectionInformation ci;
    private final static Properties prop;

    public final static String CONFIG_USERNAME = "username";
    public final static String CONFIG_PASSWORD = "password";
    public final static String CONFIG_HOST = "host";
    public final static String CONFIG_DOMAIN = "domain";
    public final static String CONFIG_CLSID = "clsid";
    public final static String CONFIG_PROGID = "progid";

    private final static String CONFIG_FILE_NAME = "/conf/utgard-opc-config.properties";

    /**
     * 加载配置文件
     */
    static {
        ci = new ConnectionInformation();
        prop = new Properties();
        try {
            prop.load(BaseConfiguration.class.getResourceAsStream(CONFIG_FILE_NAME));
        } catch (IOException e) {
            log.error("Utgard opc 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
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

    /**
     * 获得包含ClsId的连接信息
     *
     * @return
     */
    public static ConnectionInformation getCLSIDConnectionInfomation() {
        ci.setProgId(null);
        getConnectionInfomation();
        ci.setClsid(prop.getProperty(CONFIG_CLSID));
        return ci;
    }

    /**
     * 获得包含ProgId的连接信息
     *
     * @return
     */
    public static ConnectionInformation getPROGIDConnectionInfomation() {
        ci.setClsid(null);
        getConnectionInfomation();
        ci.setProgId(prop.getProperty(CONFIG_PROGID));
        return ci;
    }

    /**
     * 获得基础的连接信息
     */
    private static void getConnectionInfomation() {
        ci.setHost(prop.getProperty(CONFIG_HOST));
        ci.setDomain(prop.getProperty(CONFIG_DOMAIN));
        ci.setUser(prop.getProperty(CONFIG_USERNAME));
        ci.setPassword(prop.getProperty(CONFIG_PASSWORD));
    }
}
