package com.sq.protocol.opc.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.service.BaseService;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.openscada.opc.lib.da.browser.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Collection;
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

    public void fetchReadSyncItems (int cid) {
        OpcRegisterFactory.registerConfigItems(cid);
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        Collection<Leaf> leafs = opcServerInfomation.getLeafs();
        Server server = opcServerInfomation.getServer();
        Group group = null;
        Item[] itemArr = new Item[leafs.size()];
        try {
            int item_flag = 0;
            group = server.addGroup();
            for(Leaf leaf:leafs){
                Item item = group.addItem(leaf.getItemId());
                itemArr[item_flag] = item;
                item_flag++;
            }
            Map<Item, ItemState> syncItems = group.read(false, itemArr);
            for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
                System.out.println("key= " + entry.getKey().getId() + " and value= " + entry.getValue().getValue());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (JIException e) {
            e.printStackTrace();
        } catch (DuplicateGroupException e) {
            e.printStackTrace();
        } catch (AddFailedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册测点到指定的opc server group.
     */
    private void registerMesuringPoint(Server server) {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("mesuringPoint.meaType", 1);
    }

}