package com.sq.protocol.opc.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
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
import java.util.*;

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

    @Autowired
    @BaseComponent
    private OriginalDataRepository originalDataRepository;

    public void fetchReadSyncItems (int cid) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        if (null == opcServerInfomation.getLeafs()) {
            OpcRegisterFactory.registerConfigItems(cid);
        }
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
            readItemState(cid, group, itemArr);
        } catch (UnknownHostException e) {
            log.error("Host unknow error.",e);
        } catch (NotConnectedException e) {
            log.error("Connnect to opc error.",e);
        } catch (JIException e) {
            log.error("Opc server connect error.",e);
        } catch (DuplicateGroupException e) {
            log.error("Group duplicate error.",e);
        } catch (AddFailedException e) {
            log.error("Group add item faild.",e);
        }
    }

    /**
     * group读取item的同步值
     * @param group   opc group
     * @param itemArr item数组
     */
    public void readItemState (int cid, Group group, Item[] itemArr) {
        Map<Item, ItemState> syncItems = null;
        try {
            syncItems = group.read(false, itemArr);
        } catch (JIException e) {
            log.error("Read item error.",e);
        }
        List<OriginalData> originalDataList = new LinkedList<OriginalData>();
        OriginalData originalData = new OriginalData();
        for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
            log.error("key= " + entry.getKey().getId() + " and value= " + entry.getValue().getValue().toString());
            originalData.setItemCode(entry.getKey().getId());
            originalData.setInstanceTime(entry.getValue().getTimestamp());
            originalData.setItemValue(entry.getValue().getValue().toString());
            originalData.setSysId(cid);
            originalDataList.add(originalData);
        }
        originalDataRepository.save(originalDataList);
    }

    /**
     * 注册测点到指定的opc server group.
     */
    private void registerMesuringPoint(Server server) {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("mesuringPoint.meaType", 1);
    }

}