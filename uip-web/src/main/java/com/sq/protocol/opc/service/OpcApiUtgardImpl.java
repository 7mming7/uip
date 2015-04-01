package com.sq.protocol.opc.service;

import com.sq.protocol.opc.component.BaseConfiguration;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.da.OPCSERVERSTATE;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.FlatBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * opc服务基于utgard的实现.
 * User: shuiqing
 * Date: 2015/3/30
 * Time: 15:37
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class OpcApiUtgardImpl implements OpcApi<Server>{

    private static final Logger log = LoggerFactory.getLogger(OpcApiUtgardImpl.class);

    /**
     * 连接目标host的opc server
     * @return 服务连接
     */
    @Override
    public Server connect() {

        Server server = new Server(
                BaseConfiguration.getCLSIDConnectionInfomation(),
                Executors.newSingleThreadScheduledExecutor());

        try {
            server.connect();
        } catch (UnknownHostException e) {
            log.error("目标host的地址错误", e);
        } catch (JIException e) {
            log.error("获取配置文件中内容时出错",e);
        } catch (AlreadyConnectedException e) {
            log.error("连接已经存在，无需再次连接",e);
        }
        return server;
    }

    /**
     * 断开与目标host地址上opc server的连接
     */
    @Override
    public void closeConnection(Server server) {
        server.dispose();
    }
}
