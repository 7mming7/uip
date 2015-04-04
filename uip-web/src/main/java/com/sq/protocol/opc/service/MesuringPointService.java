package com.sq.protocol.opc.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.UtgardOpcHelper;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.service.BaseService;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 测点相关业务类.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 15:01
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class MesuringPointService extends BaseService<MesuringPoint, Long> {

    private static final Logger log = LoggerFactory.getLogger(MesuringPointService.class);

    @Autowired
    @BaseComponent
    private MesuringPointRepository mesuringPointRepository;

    /**
     * 获取opc批量数据
     */
    public void fetchSyncItems (int cid) {
        Server server = UtgardOpcHelper.connect(cid);
        try {
            Branch branch  = server.getTreeBrowser().browse();
            dumpTree(branch, 0);
        } catch (UnknownHostException e) {
            log.error("Host name is error,please check it.", e);
        } catch (JIException e) {
            log.error("Connect to server error.", e);
        }
    }

    /**
     * 注册测点到指定的opc server group.
     */
    private void registerMesuringPoint(Server server) {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("mesuringPoint.meaType", 1);
    }

    /**
     * 挖掘整个测点树
     * @param branch
     * @param level
     */
    private static void dumpTree(final Branch branch, final int level) {
        for (final Leaf leaf : branch.getLeaves()) {
            /*dumpLeaf(leaf, level);*/
        }
        for (final Branch subBranch : branch.getBranches()) {
            dumpBranch(subBranch, level);
            dumpTree(subBranch, level + 1);
        }
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
        System.out.println(printTab(level) + "Leaf: " + leaf.getName() + ":"
                + leaf.getItemId());
    }

    /**
     * 打印Group
     *
     * @param branch
     */
    private static void dumpBranch(final Branch branch, final int level) {
        System.out.println(printTab(level) + "Branch: " + branch.getName());
    }
}
