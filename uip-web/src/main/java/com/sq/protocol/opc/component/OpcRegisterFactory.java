package com.sq.protocol.opc.component;

import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * OPC服务注册中心.
 * User: shuiqing
 * Date: 2015/4/4
 * Time: 16:41
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class OpcRegisterFactory {

    private static final Logger log = LoggerFactory.getLogger(OpcRegisterFactory.class);

    /** opc连接配置信息  K：clientID V：配置信息*/
    private static Map<Integer, OpcServerInfomation> conInfoMap = new ConcurrentHashMap<Integer, OpcServerInfomation>();

    /** 测点实时数据缓存 */
    public static Map<String, String> mesuringPointCacheMap = new ConcurrentHashMap<String, String>();

    /**
     * 注册连接信息，K 为server_id，V 为具体的连接信息
     * @param c_id server id
     * @param serverInfomation 服务信息
     */
    public static void registerServerInfo (int c_id, OpcServerInfomation serverInfomation) {
        conInfoMap.put(c_id,serverInfomation);
    }

    /**
     * 填充注册中心关于server下的item.
     */
    public static void registerConfigItems (int cid) {
        Server server = UtgardOpcHelper.connect(cid);
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        opcServerInfomation.setServer(server);
        fillItemAutoGenerate(opcServerInfomation, server);
    }

    /**
     * 填充注册中心关于server下的item,根据数据库中的测点名称
     * @param cid
     * @param mesuringPointList
     */
    public static void registerConfigItems (int cid, List<MesuringPoint> mesuringPointList) {
        Server server = UtgardOpcHelper.connect(cid);
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        opcServerInfomation.setServer(server);
        opcServerInfomation.setConn_status(true);
        fillItemDbRecord(opcServerInfomation, mesuringPointList);
    }

    /**
     * 根据opc服务上的item自动填充同步ITEM
     * @param opcServerInfomation
     * @param server
     */
    private static void fillItemAutoGenerate (OpcServerInfomation opcServerInfomation, Server server) {
        Collection<Leaf> leafs = new LinkedList<Leaf>();
        try {
            Branch branch  = server.getTreeBrowser().browse();
            leafs = dumpTreeLeaf(branch, 0, null);
            opcServerInfomation.setLeafs(leafs);
        } catch (UnknownHostException e) {
            log.error("Host name is error,please check it.", e);
        } catch (JIException e) {
            log.error("Connect to server error.", e);
        }
    }

    /**
     * 根据sysId获取opc服务的item
     */
    private static void fillItemDbRecord (OpcServerInfomation opcServerInfomation,
                                           List<MesuringPoint> mesuringPointList) {
        Collection<Leaf> leafs = new LinkedList<Leaf>();
        for (MesuringPoint mesuringPoint:mesuringPointList) {
            Leaf leaf = new Leaf(null, mesuringPoint.getSourceCode(), mesuringPoint.getSourceCode());
            leafs.add(leaf);
        }
        opcServerInfomation.setLeafs(leafs);
    }

    public static OpcServerInfomation fetchOpcInfo (int c_id) {
        return conInfoMap.get(c_id);
    }

    public static ConnectionInformation fetchConnInfo (int c_id) {
        return conInfoMap.get(c_id).getConnectionInformation();
    }

    /**
     * 更改opc连接的状态
     * 自定义标示，标示当前连接是否需要重连
     * @param cid
     */
    public static void changeConnStatus (int cid) {
        OpcServerInfomation opcServerInfomation = conInfoMap.get(cid);
        opcServerInfomation.setConn_status(false);
    }

    /**
     * 挖掘整个测点树的叶子节点
     * @param branch
     * @param level
     */
    private static LinkedList<Leaf> dumpTreeLeaf(final Branch branch, final int level, LinkedList<Leaf> leafs) {
        if (null == leafs) {
            leafs = new LinkedList<Leaf>();
        }
        for (Leaf leaf : branch.getLeaves()) {
            dumpLeaf(leaf, level);
            leafs.add(leaf);
        }
        for (Branch subBranch : branch.getBranches()) {
            /*dumpBranch(subBranch, level);*/
            dumpTreeLeaf(subBranch, level + 1, leafs);
        }
        return leafs;
    }

    /**
     * 打印Tab符
     * @param level
     * @return
     */
    private static String printTab(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    /**
     * 打印Item
     *
     * @param leaf
     */
    private static void dumpLeaf(final Leaf leaf, final int level) {
        log.error(printTab(level) + "Leaf: " + leaf.getName() + ":"
                + leaf.getItemId());
    }

    /**
     * 打印Group
     *
     * @param branch
     */
    private static void dumpBranch(final Branch branch, final int level) {
        log.error(printTab(level) + "Branch: " + branch.getName());
    }
}

