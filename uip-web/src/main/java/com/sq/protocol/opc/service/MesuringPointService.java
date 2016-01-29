package com.sq.protocol.opc.service;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.BaseConfiguration;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.service.BaseService;
import com.sq.util.NumberUtils;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIUnsignedInteger;
import org.jinterop.dcom.core.JIUnsignedShort;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.openscada.opc.lib.da.browser.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.*;

import static com.sq.protocol.opc.domain.ItemFillType.AutoGenerate;
import static com.sq.protocol.opc.domain.ItemFillType.DbRecord;

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
    private OriginalDataRepository originalDataRepository;

    /**
     * 读取server下所有的ITEM
     * @param cid
     */
    public void fetchReadSyncItems (final int cid) {
        boolean flag = false;
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        log.debug(opcServerInfomation.toString());
        if (opcServerInfomation.getLeafs() == null || !opcServerInfomation.isConn_status()) {
            opcServerInfomation.setLeafs(null);
            switch (BaseConfiguration.CONFIG_INIT_ITEM) {
                case AutoGenerate:
                    OpcRegisterFactory.registerConfigItems(cid);
                    break;
                case DbRecord:
                    List<MesuringPoint> mesuringPointList = this.registerMesuringPoint(cid);
                    OpcRegisterFactory.registerConfigItems(cid, mesuringPointList);
                    break;
            }
            flag = true;
        }
        Collection<Leaf> leafs = opcServerInfomation.getLeafs();
        Server server = opcServerInfomation.getServer();
        server.setDefaultUpdateRate(6000);
        try {
            if (flag) {
                fillInOpcItemToServer(server, leafs, cid);
            }

            Map<Item, ItemState> syncItems = null;
            try {
                /** arg1 false 读取缓存数据 OPCDATASOURCE.OPC_DS_CACHE  */
                syncItems = opcServerInfomation.getGroup().read(true, opcServerInfomation.getItemArr());
            } catch (JIException e) {
                log.error("Read item error.",e);
            }

            readItemStateMysql(cid, syncItems);
            /*final Group finalGroup = group;
            new Thread("mysql_opc_sync_thread"){
                @Override
                public void run() {
                    readItemStateMysql(cid, finalGroup, itemArr);
                }
            }.start();*/
            /*new Thread("mongo_opc_sync_thread"){
                @Override
                public void run() {
                    readItemStateMongo(cid, finalGroup, itemArr);
                }
            }.start();*/

        } catch (UnknownHostException e) {
            log.error("Host unknow error.",e);
        } catch (NotConnectedException e) {
            OpcRegisterFactory.fetchOpcInfo(cid).setConn_status(false);
            log.error("Connnect to opc error.",e);
        } catch (JIException e) {
            log.error("Opc server connect error.",e);
        } catch (DuplicateGroupException e) {
            log.error("Group duplicate error.",e);
        }
    }

    /**
     * 添加通讯测点
     * @param server 已经连接的通讯通道
     * @param leafs 通讯测点集合
     * @throws DuplicateGroupException
     * @throws NotConnectedException
     * @throws JIException
     * @throws UnknownHostException
     */
    public void fillInOpcItemToServer (Server server,Collection<Leaf> leafs, int cid)
            throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException {
        SynchronizedCollection<Leaf> synchronizedCollection = SynchronizedCollection.synchronizedCollection(leafs);
        log.debug("开始添加测点.");
        int item_flag = 0;
        Group group = server.addGroup();
        group.setActive(true);
        Item[] itemArr = new Item[leafs.size()];
        for(Leaf leaf:synchronizedCollection) {
            Item item = null;
            try {
                item = group.addItem(leaf.getItemId());
            } catch (AddFailedException e) {
                log.error("Group add error.itemCode：" + leaf.getItemId(), e);
                leafs.remove(leaf);
                fillInOpcItemToServer(server, leafs, cid);
                return;
            }
            item.setActive(true);
            log.debug("ItemName:[" + item.getId()
                    + "],value:" + item.read(true).getValue());
            itemArr[item_flag] = item;
            item_flag++;
        }
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        opcServerInfomation.setGroup(group);
        opcServerInfomation.setItemArr(itemArr);
    }

    /**
     * group读取item的同步值 mysql
     */
    public void readItemStateMysql (int cid, Map<Item, ItemState> syncItems) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        log.debug(opcServerInfomation.toString());
        log.debug("syncItems size:" + syncItems.size());
        Long batchNum = originalDataRepository.gernateNextBatchNumber(Integer.parseInt(opcServerInfomation.getSysId()));
        List<OriginalData> originalDataList = new LinkedList<OriginalData>();
        for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
            String itemValue = entry.getValue().getValue().toString();
            String itemCode = entry.getKey().getId();
            try {
                log.debug("key= " + entry.getKey().getId()
                        + " and value= " + entry.getValue().getValue().toString()
                        + " and type= " + entry.getValue().getValue().getType());
                JIVariant jiVariant = entry.getValue().getValue();
                if (jiVariant.getType() == 8) {
                    JIString jiString = (JIString)jiVariant.getObject();
                    itemValue = jiString.getString();
                }else if (jiVariant.getType() == 18) {
                    JIUnsignedShort jiUnsignedShort = (JIUnsignedShort)jiVariant.getObject();
                    itemValue = jiUnsignedShort.getValue().toString();
                } else if (jiVariant.getType() == 19) {
                    JIUnsignedInteger jiUnsignedInteger = (JIUnsignedInteger)jiVariant.getObject();
                    itemValue = jiUnsignedInteger.getValue().toString();
                } else {
                    itemValue = jiVariant.getObject().toString();
                }
                log.debug("&&&&&type-> " + jiVariant.getType() + " ,value -->  " + itemValue);
            } catch (JIException e) {
                log.error("获取JIVariant数据出错.",e);
            }
            if (itemValue.contains("org.jinterop.dcom.core.VariantBody$EMPTY")) {
                continue;
            }
            OriginalData originalData = new OriginalData();
            originalData.setItemCode(itemCode);
            originalData.setInstanceTime(entry.getValue().getTimestamp());
            originalData.setItemValue(itemValue);
            originalData.setSysId(Integer.parseInt(opcServerInfomation.getSysId()));
            originalData.setBatchNum(batchNum);
            originalDataList.add(originalData);
        }
        originalDataRepository.save(originalDataList);
    }

    /**
     * 接收UDP传入的数据并保存到mysql服务中
     * @param receiveMsg
     */
    public void receiveUdpDataMysql (int sysId, String receiveMsg) {
        String[] pointEntityArray = receiveMsg.split(";");
        Long batchNum = originalDataRepository.gernateNextBatchNumber(sysId);
        List<OriginalData> originalDataList = new LinkedList<OriginalData>();
        for (String pointStr:pointEntityArray) {
            String[] paramArray = pointStr.split("%%");
            OriginalData originalData = new OriginalData();
            originalData.setInstanceTime(Calendar.getInstance());
            originalData.setBatchNum(batchNum);
            originalData.setItemCode(paramArray[0]);
            if (NumberUtils.isNumeric(paramArray[1]))
                originalData.setItemValue(paramArray[1].trim());
            else
                originalData.setItemValue("");
            originalData.setSysId(sysId);
            originalDataList.add(originalData);
        }
        originalDataRepository.save(originalDataList);
    }

    /**
     * 注册测点到指定的opc server group.
     */
    private List<MesuringPoint> registerMesuringPoint(int cid) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("sysId", MatchType.EQ, opcServerInfomation.getSysId());
        return this.findAllWithNoPageNoSort(searchable);
    }
}