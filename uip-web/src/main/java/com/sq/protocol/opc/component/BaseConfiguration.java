package com.sq.protocol.opc.component;

import com.sq.protocol.opc.domain.ItemFillType;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
@Component
public class BaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BaseConfiguration.class);

    private static Properties prop;

    public static String CONFIG_USERNAME = "username";
    public static String CONFIG_PASSWORD = "password";
    public static String CONFIG_HOST = "host";
    public static String CONFIG_DOMAIN = "domain";
    public static String CONFIG_CLSID = "clsid";
    public static String CONFIG_PROGID = "progid";
    public static String CONFIG_SYSID = "sysid";

    /** 客户端配置的初始序列 */
    public static int CONFIG_CLIENT_ID = 1;

    /** 客户端配置允许的最大序列 */
    public static int CONFIG_CLIENT_MAX;

    /** 支持的最大的客户端的数量 */
    public static String CONFIG_MAX_CLIENT = "max_client";

    /** 需要同步读取的ITEM的初始化方式 默认为1 */
    public static int CONFIG_INIT_ITEM = ItemFillType.DbRecord;

    /** 需要同步读取的ITEM的初始化方式 */
    public static String CONFIG_INIT_ITEM_TYPE = "init_item_type";

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/utgard-opc-config.properties";

    /** 字符串连接符 */
    public static final String connOper = "->";

    /**
     * 加载配置文件
     */
    static {
        loadConfigProperties();
        fillOpcConnInformation();
    }

    /**
     * 加载opc服务的配置文件
     * @return
     */
    private static Properties loadConfigProperties () {
        prop = new Properties();
        try {
            prop.load(BaseConfiguration.class.getResourceAsStream(CONFIG_FILE_NAME));
            CONFIG_CLIENT_MAX = Integer.parseInt(getEntryValue(CONFIG_MAX_CLIENT));
            CONFIG_INIT_ITEM = Integer.parseInt(getEntryValue(CONFIG_INIT_ITEM_TYPE));
        } catch (IOException e) {
            log.error("Utgard opc 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    /**
     * 将配置文件中配置的客户端连接信息全都填充到MAP对象中
     * @return 包含了所有的OPC 客户端连接信息的MAP对象
     */
    private static void fillOpcConnInformation () {
        while (CONFIG_CLIENT_ID <= CONFIG_CLIENT_MAX) {
            assembleConfigKeyValue(CONFIG_CLIENT_ID);
            CONFIG_CLIENT_ID++;
        }
    }

    /**
     * 根据当前的客户端的ID拼装连接信息
     * @param client_id 客户端ID
     */
    public static void assembleConfigKeyValue (Integer client_id) {
        OpcServerInfomation opcServerInfomation = new OpcServerInfomation();
        ConnectionInformation connectionInformation = new ConnectionInformation();
        String slink = connOper + client_id.toString();
        connectionInformation.setUser(getEntryValue(CONFIG_USERNAME + slink));
        connectionInformation.setClsid(getEntryValue(CONFIG_CLSID + slink));
        connectionInformation.setPassword(getEntryValue(CONFIG_PASSWORD + slink));
        connectionInformation.setDomain(getEntryValue(CONFIG_DOMAIN + slink));
        connectionInformation.setHost(getEntryValue(CONFIG_HOST + slink));
        connectionInformation.setProgId(getEntryValue(CONFIG_PROGID + slink));
        opcServerInfomation.setC_id(client_id);
        opcServerInfomation.setSysId(getEntryValue(CONFIG_SYSID + slink));
        opcServerInfomation.setConnectionInformation(connectionInformation);
        OpcRegisterFactory.registerServerInfo(client_id, opcServerInfomation);
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
    public static ConnectionInformation getCLSIDConnectionInfomation(Integer client_id) {
        ConnectionInformation ci = OpcRegisterFactory.fetchConnInfo(client_id);
        ci.setProgId(null);
        ci.setClsid(ci.getClsid());
        return ci;
    }

    /**
     * 获得包含ProgId的连接信息
     *
     * @return
     */
    public static ConnectionInformation getPROGIDConnectionInfomation(Integer client_id) {
        ConnectionInformation ci = OpcRegisterFactory.fetchConnInfo(client_id);
        ci.setClsid(null);
        ci.setProgId(ci.getProgId());
        return ci;
    }
}
