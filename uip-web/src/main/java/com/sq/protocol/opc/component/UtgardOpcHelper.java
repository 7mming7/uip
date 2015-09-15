package com.sq.protocol.opc.component;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.FlatBrowser;
import org.openscada.opc.lib.list.Categories;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Executors;

/**
 * opc连接辅助类.
 * User: shuiqing
 * Date: 2015/3/30
 * Time: 11:09
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class UtgardOpcHelper {

    private static final Logger log = LoggerFactory.getLogger(UtgardOpcHelper.class);

    /**
     * 连接目标host的opc server
     * @return 服务连接
     */
    public static Server connect(int sid) {

        Server server = new Server(
                BaseConfiguration.getCLSIDConnectionInfomation(sid),
                Executors.newSingleThreadScheduledExecutor());
        server.setDefaultLocaleID(sid);
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
    public void closeConnection(Server server) {
        server.dispose();
    }

    /**
     * 获取目标地址下所有的opc服务的详细信息
     * @return
     * @throws JIException
     * @throws UnknownHostException
     */
    public Collection<ClassDetails> fetchClassDetails (int sid) {
        Collection<ClassDetails> classDetails = null;

        try {
            ConnectionInformation connectionInformation = OpcRegisterFactory.fetchConnInfo(sid);
            ServerList serverList = new ServerList(connectionInformation.getHost(),
                    connectionInformation.getUser(), connectionInformation.getPassword(),
                    connectionInformation.getDomain());

            /** According the progid get the clsid, then get the classdetail */
            /** Whatever the using DA agreement */
            classDetails = serverList
                    .listServersWithDetails(new Category[] {
                            Categories.OPCDAServer10, Categories.OPCDAServer20,
                            Categories.OPCDAServer30 }, new Category[] {});

            log.error("-----------------------------------------------------------");
            log.error("--------开始获取目标Ip：" + connectionInformation.getHost() + "下所有on service的opc服务.-----");
            for (ClassDetails cds : classDetails) {
                log.error("ClassDetails  Show.   ");
                log.error("    ProgId--->>" + cds.getProgId());
                log.error("    Desp  --->>" + cds.getDescription());
                log.error("    ClsId --->>" + cds.getClsId());
            }
            log.error("-----------------------------------------------------------");
        } catch (JIException e) {
            log.error("获取配置文件中内容时出错",e);
        } catch (UnknownHostException e1) {
            log.error("Host无法识别或者格式错误",e1);
        }

        return classDetails;
    }

    /**
     * 当前opc connection的状态信息
     * @param server
     */
    public void dumpServerStatus(final Server server) {
        final OPCSERVERSTATUS status = server.getServerState();

        log.info("===== SERVER STATUS ======");
        log.info("State: " + status.getServerState().toString());
        log.info("Vendor: " + status.getVendorInfo());
        log.info(String.format("Version: %d.%d.%d",
                status.getMajorVersion(), status.getMinorVersion(),
                status.getBuildNumber()));
        log.info("Groups: " + status.getGroupCount());
        log.info("Bandwidth: " + status.getBandWidth());
        log.info(String.format("Start Time: %tc", status.getStartTime().asCalendar()));
        log.info(String.format("Current Time: %tc", status.getCurrentTime().asCalendar()));
        log.info(String.format("Last Update Time: %tc", status.getLastUpdateTime().asCalendar()));
        log.info("===== SERVER STATUS ======");
    }

    /**
     * 同步指定item
     */
    public JIVariant syncTargetItem (Item item) {
        Assert.notNull(item, "Target item must not be null.");
        JIVariant ji = null;
        try {
            ji = item.read(false).getValue();
        } catch (JIException e) {
            log.error("获取" + item.getId() + "的实时值出错.",e);
        }
        return ji;
    }

    public static void main (String args[]) {
        UtgardOpcHelper ut = new UtgardOpcHelper();
        Collection<ClassDetails> classDetails = null;

        try {
            ConnectionInformation connectionInformation = new ConnectionInformation();
            connectionInformation.setHost("localhost");
            connectionInformation.setUser("Administrator");
            connectionInformation.setPassword("123456");
            connectionInformation.setDomain("");
            ServerList serverList = new ServerList(connectionInformation.getHost(),
                    connectionInformation.getUser(), connectionInformation.getPassword(),
                    connectionInformation.getDomain());

            /** According the progid get the clsid, then get the classdetail */
            /** Whatever the using DA agreement */
            classDetails = serverList
                    .listServersWithDetails(new Category[] {
                            Categories.OPCDAServer10, Categories.OPCDAServer20,
                            Categories.OPCDAServer30 }, new Category[] {});

            log.error("-----------------------------------------------------------");
            log.error("--------开始获取目标Ip：" + connectionInformation.getHost() + "下所有on service的opc服务.-----");
            for (ClassDetails cds : classDetails) {
                log.error("ClassDetails  Show.   ");
                log.error("    ProgId--->>" + cds.getProgId());
                log.error("    Desp  --->>" + cds.getDescription());
                log.error("    ClsId --->>" + cds.getClsId());
            }
            log.error("-----------------------------------------------------------");
        } catch (JIException e) {
            log.error("获取配置文件中内容时出错",e);
        } catch (UnknownHostException e1) {
            log.error("Host无法识别或者格式错误",e1);
        }
    }
}
