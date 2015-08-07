package com.sq.protocol.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
public class UdpSocketConfig {

    private static final Logger log = LoggerFactory.getLogger(UdpSocketConfig.class);

    /** 配置文件路径 */
    private static Properties prop;

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/socket-config.properties";

    public static Map<Integer, UdpConnectInfo> udpConnectInfoMap = new HashMap<Integer, UdpConnectInfo>();

    public static String CONFIG_LISTENING_PORT = "LISTENING_SOCKET_PORT";

    public static String CONFIG_SYSID = "SYSID";

    /** 客户端配置的初始序列 */
    public static int CONFIG_CLIENT_ID = 1;

    /** 客户端配置允许的最大序列 */
    public static int CLIENT_MAX;

    /** 支持的最大的客户端的数量 */
    public static String CONFIG_MAX_CLIENT = "MAX_CLIENT";

    /** 字符串连接符 */
    public static final String connOper = "->";

    /** socket服务开关 */
    public static boolean SOCKET_RUNNING_SWITCH = true;

    static {
        loadConfigProperties();
        fillUdpConnInformation();
    }

    /**
     * 加载socket服务的配置文件
     * @return
     */
    private static Properties loadConfigProperties () {
        prop = new Properties();
        try {
            prop.load(UdpSocketConfig.class.getResourceAsStream(CONFIG_FILE_NAME));
            CLIENT_MAX = Integer.parseInt(getEntryValue(CONFIG_MAX_CLIENT));
        } catch (IOException e) {
            log.error("Socket conf 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    /**
     * 将配置文件中配置的客户端连接信息全都填充到MAP对象中
     * @return 包含了所有的OPC 客户端连接信息的MAP对象
     */
    private static void fillUdpConnInformation () {
        while (CONFIG_CLIENT_ID <= CLIENT_MAX) {
            assembleConfigKeyValue(CONFIG_CLIENT_ID);
            CONFIG_CLIENT_ID++;
        }
    }

    /**
     * 根据当前的客户端的ID拼装连接信息
     * @param client_id 客户端ID
     */
    public static void assembleConfigKeyValue (Integer client_id) {
        UdpConnectInfo udpConnectInfo = new UdpConnectInfo();
        String slink = connOper + client_id.toString();
        udpConnectInfo.setC_id(client_id);
        udpConnectInfo.setSysId(Integer.parseInt(getEntryValue(CONFIG_SYSID + slink)));
        udpConnectInfo.setListening_port(Integer.parseInt(getEntryValue(CONFIG_LISTENING_PORT + slink)));
        udpConnectInfoMap.put(client_id,udpConnectInfo);
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