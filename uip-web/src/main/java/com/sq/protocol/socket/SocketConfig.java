package com.sq.protocol.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * Socket配置对象.
 * User: shuiqing
 * Date: 2015/5/20
 * Time: 11:58
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class SocketConfig {

    private static final Logger log = LoggerFactory.getLogger(SocketConfig.class);

    /** 配置文件路径 */
    private static Properties prop;

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/socket-config.properties";

    /** socket服务的端口号 */
    public final static String CONFIG_SOCKET_PORT = "socket_port";

    /** 配置在配置文件中的SOCKET的端口号 */
    public static int SOCKET_PORT;

    /** socket服务开关 */
    public static boolean SOCKET_RUNNING_SWITCH = true;

    static {
        loadConfigProperties();
    }

    /**
     * 加载socket服务的配置文件
     * @return
     */
    private static Properties loadConfigProperties () {
        prop = new Properties();
        try {
            prop.load(SocketConfig.class.getResourceAsStream(CONFIG_FILE_NAME));
            SOCKET_PORT = Integer.parseInt(getEntryValue(CONFIG_SOCKET_PORT));
        } catch (IOException e) {
            log.error("Socket conf 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
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
