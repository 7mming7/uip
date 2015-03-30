package com.sq.protocol.opc.component;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.list.Categories;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Executors;

import static com.sq.protocol.opc.component.BaseConfiguration.getEntryValue;

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
     * 获取目标地址下所有的opc服务的详细信息
     * @return
     * @throws JIException
     * @throws UnknownHostException
     */
    public Collection<ClassDetails> fetchClassDetails () {
        Collection<ClassDetails> classDetails = null;

        try {
            ServerList serverList = new ServerList(getEntryValue(BaseConfiguration.CONFIG_HOST),
                    getEntryValue(BaseConfiguration.CONFIG_USERNAME), getEntryValue(BaseConfiguration.CONFIG_PASSWORD),
                    getEntryValue(BaseConfiguration.CONFIG_DOMAIN));

            /** According the progid get the clsid, then get the classdetail */
            /** Whatever the using DA agreement */
            classDetails = serverList
                    .listServersWithDetails(new Category[] {
                            Categories.OPCDAServer10, Categories.OPCDAServer20,
                            Categories.OPCDAServer30 }, new Category[] {});

            log.info("-----------------------------------------------------------");
            log.info("--------开始获取目标Ip：" + BaseConfiguration.CONFIG_HOST + "下所有on service的opc服务.-----");
            for (ClassDetails cds : classDetails) {
                log.info("ClassDetails  Show.   ");
                log.info("    ProgId--->>" + cds.getProgId());
                log.info("    Desp  --->>" + cds.getDescription());
                log.info("    ClsId --->>" + cds.getClsId());
            }
            log.info("-----------------------------------------------------------");
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

    public static void main (String args[]) {
        UtgardOpcHelper ut = new UtgardOpcHelper();
        ut.fetchClassDetails();

        Server server = new Server(
                BaseConfiguration.getCLSIDConnectionInfomation(),
                Executors.newSingleThreadScheduledExecutor());
        try {
            server.connect();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (JIException e) {
            e.printStackTrace();
        } catch (AlreadyConnectedException e) {
            e.printStackTrace();
        }
        ut.dumpServerStatus(server);
    }
}
