package com.sq.protocol.opc.service;

import com.mongo.domain.MongoOrignalDataRealTime;
import com.mongo.repository.MongoOrignalDataRealTimeRespository;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.BaseConfiguration;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.mongo.domain.MongoOriginalDataHistory;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.mongo.repository.MongoOrignalDataHistoryRespository;
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

    @Autowired
    private MongoOrignalDataHistoryRespository mongoOrignalDataHistoryRespository;

    @Autowired
    private MongoOrignalDataRealTimeRespository mongoOrignalDataRealTimeRespository;

    /**
     * 实时数据缓存
     */
    private static Map<Integer,Map<String,MongoOrignalDataRealTime>> mongoOrignalCacheMap = new HashMap<Integer,Map<String,MongoOrignalDataRealTime>>();

    /**
     * 读取server下所有的ITEM
     * @param cid
     */
    public void fetchReadSyncItems (final int cid) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        if (opcServerInfomation.getLeafs() == null || !opcServerInfomation.isConn_status()) {
            opcServerInfomation.setLeafs(null);
            switch (BaseConfiguration.CONFIG_INIT_ITEM) {
                case AutoGenerate:
                    OpcRegisterFactory.registerConfigItems(cid);
                    break;
                case DbRecord:
                    List<MesuringPoint> mesuringPointList = this.registerMesuringPoint(cid);
                    mongoOrignalRealTimeDataCache(cid,mesuringPointList);
                    OpcRegisterFactory.registerConfigItems(cid, mesuringPointList);
                    break;
            }
        }
        Collection<Leaf> leafs = opcServerInfomation.getLeafs();
        Server server = opcServerInfomation.getServer();
        Group group = null;
        final Item[] itemArr = new Item[leafs.size()];
        try {
            int item_flag = 0;
            group = server.addGroup();
            for(Leaf leaf:leafs){
                Item item = group.addItem(leaf.getItemId());
                System.out.println("ItemName:[" + item.getId()
                        + "],value:" + item.read(true).getValue());
                itemArr[item_flag] = item;
                item_flag++;
            }
            readItemStateMongo(cid, group, itemArr);
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
        } catch (AddFailedException e) {
            log.error("Group add error.",e);
        }
    }

    /**
     * group读取item的同步值 mysql
     * @param group   opc group
     * @param itemArr item数组
     */
    public void readItemStateMysql (int cid, Group group, Item[] itemArr) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        Map<Item, ItemState> syncItems = null;
        try {
            /** arg1 false 读取缓存数据 OPCDATASOURCE.OPC_DS_CACHE  */
            syncItems = group.read(true, itemArr);
        } catch (JIException e) {
            log.error("Read item error.",e);
        }
        Long batchNum = originalDataRepository.gernateNextBatchNumber(Integer.parseInt(opcServerInfomation.getSysId()));
        List<OriginalData> originalDataList = new LinkedList<OriginalData>();
        for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
            log.error("key= " + entry.getKey().getId()
                    + " and value= " + entry.getValue().getValue().toString());
            String itemValue = entry.getValue().getValue().toString();
            if (itemValue.contains("org.jinterop.dcom.core.VariantBody$EMPTY")) {
                continue;
            }
            OriginalData originalData = new OriginalData();
            originalData.setItemCode(entry.getKey().getId());
            originalData.setInstanceTime(entry.getValue().getTimestamp());
            originalData.setItemValue(itemValue.substring(2, itemValue.length() - 2));
            originalData.setSysId(Integer.parseInt(opcServerInfomation.getSysId()));
            originalData.setBatchNum(batchNum);
            originalDataList.add(originalData);
        }
        originalDataRepository.save(originalDataList);
    }

    /**
     * group读取item的同步值 mongo
     * @param group   opc group
     * @param itemArr item数组
     */
    public void readItemStateMongo (int cid, Group group, Item[] itemArr) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        Map<Item, ItemState> syncItems = null;
        try {
            syncItems = group.read(true, itemArr);
        } catch (JIException e) {
            log.error("Read item error.",e);
        }
        Long batchNum = originalDataRepository.gernateNextBatchNumber(Integer.parseInt(opcServerInfomation.getSysId()));
        List<MongoOriginalDataHistory> originalDataHistoryList = new LinkedList<MongoOriginalDataHistory>();
        List<MongoOrignalDataRealTime> originalDataRealTimeList = new LinkedList<MongoOrignalDataRealTime>();
        for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
            log.error("key= " + entry.getKey().getId()
                    + " and value= " + entry.getValue().getValue().toString());
            String itemValue = entry.getValue().getValue().toString();
            if (itemValue.contains("org.jinterop.dcom.core.VariantBody$EMPTY")) {
                continue;
            }
            MongoOriginalDataHistory originalData = new MongoOriginalDataHistory();
            originalData.setItemCode(entry.getKey().getId());
            originalData.setInstanceTime(entry.getValue().toString());
            originalData.setItemValue(itemValue.substring(2, itemValue.length() - 2));
            originalData.setSysId(Integer.parseInt(opcServerInfomation.getSysId()));
            originalData.setBatchNum(batchNum);
            originalDataHistoryList.add(originalData);

            /*MongoOrignalDataRealTime originalDataRealTime =
                    mongoOrignalCacheMap.get(
                            Integer.parseInt(
                                    OpcRegisterFactory.fetchOpcInfo(cid).getSysId())).get(entry.getKey().getId());
            originalDataRealTime.setItemCode(entry.getKey().getId());
            originalDataRealTime.setInstanceTime(entry.getValue().toString());
            originalDataRealTime.setItemValue(itemValue.substring(2, itemValue.length() - 2));
            originalDataRealTime.setSysId(Integer.parseInt(opcServerInfomation.getSysId()));
            originalDataRealTime.setBatchNum(batchNum);
            originalDataRealTimeList.add(originalDataRealTime);*/
        }
        mongoOrignalDataHistoryRespository.save(originalDataHistoryList);
        /*mongoOrignalDataRealTimeRespository.save(
                mongoOrignalCacheMap.get(Integer.parseInt(OpcRegisterFactory.fetchOpcInfo(cid).getSysId())).values());*/
    }

    /**
     * 缓存mongo中的实时数据
     * @param mesuringPointList 系统测点列表
     */
    private void mongoOrignalRealTimeDataCache (int cid,List<MesuringPoint> mesuringPointList) {
        List<MongoOrignalDataRealTime> mongoOrignalDataRealTimeList =
                mongoOrignalDataRealTimeRespository.findBySysId(Integer.parseInt(OpcRegisterFactory.fetchOpcInfo(cid).getSysId()));

        List<MongoOrignalDataRealTime> needAddRealTimeList = new LinkedList<MongoOrignalDataRealTime>();
        List<MongoOrignalDataRealTime> needDeleteRealTimeList = new LinkedList<MongoOrignalDataRealTime>();
        for (MesuringPoint mesuringPoint:mesuringPointList) {
            for (MongoOrignalDataRealTime mongoOrignalDataRealTime:mongoOrignalDataRealTimeList) {
                if(mesuringPoint.getSourceCode().equals(mongoOrignalDataRealTime.getItemCode())) {
                    break;
                }
                needAddRealTimeList.add(point2realTimeData(cid,mesuringPoint));
            }
        }

        for (MongoOrignalDataRealTime mongoOrignalDataRealTime:mongoOrignalDataRealTimeList) {
            for (MesuringPoint mesuringPoint:mesuringPointList) {
                if(mesuringPoint.getSourceCode().equals(mongoOrignalDataRealTime.getItemCode())) {
                    break;
                }
                needDeleteRealTimeList.add(mongoOrignalDataRealTime);
            }
        }

        mongoOrignalDataRealTimeList.addAll(needAddRealTimeList);
        mongoOrignalDataRealTimeList.removeAll(needDeleteRealTimeList);
        mongoOrignalDataRealTimeRespository.save(mongoOrignalDataRealTimeList);

        Map<String,MongoOrignalDataRealTime> sysRealOrignalDataCacheMap =
                mongoOrignalCacheMap.get(Integer.parseInt(OpcRegisterFactory.fetchOpcInfo(cid).getSysId()));
        for (MongoOrignalDataRealTime mongoOrignalDataRealTime:mongoOrignalDataRealTimeList) {
            sysRealOrignalDataCacheMap.put(mongoOrignalDataRealTime.getItemCode(),mongoOrignalDataRealTime);
        }
    }

    /**
     * 测点转换为实时数据
     * @return 测点实时数据
     */
    public MongoOrignalDataRealTime point2realTimeData (int cid,MesuringPoint mesuringPoint) {
        MongoOrignalDataRealTime mongoOrignalDataRealTime = new MongoOrignalDataRealTime();
        mongoOrignalDataRealTime.setItemCode(mesuringPoint.getSourceCode());
        mongoOrignalDataRealTime.setSysId(Integer.parseInt(OpcRegisterFactory.fetchOpcInfo(cid).getSysId()));
        return mongoOrignalDataRealTime;
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