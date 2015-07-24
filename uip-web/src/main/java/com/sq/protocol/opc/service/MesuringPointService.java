package com.sq.protocol.opc.service;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.comput.service.LimitInstanceService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.jodbc.domain.JodbcConsts;
import com.sq.protocol.opc.component.BaseConfiguration;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.domain.MeaType;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
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
    private IndicatorTempRepository indicatorTempRepository;

    @Autowired
    private LimitInstanceService limitInstanceService;

    @Autowired
    private IndicatorInstanceRepository indicatorInstanceRepository;

    /**
     * 读取server下所有的ITEM
     * @param cid
     */
    public void fetchReadSyncItems (int cid) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        if (opcServerInfomation.getLeafs() == null) {
            switch (BaseConfiguration.CONFIG_INIT_ITEM) {
                case AutoGenerate:
                    OpcRegisterFactory.registerConfigItems(cid);
                    break;
                case DbRecord:
                    List<MesuringPoint> mesuringPointList = this.registerMesuringPoint(cid);
                    OpcRegisterFactory.registerConfigItems(cid, mesuringPointList);
                    break;
            }
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
            log.error("Group add error.",e);
        }
    }

    /**
     * group读取item的同步值
     * @param group   opc group
     * @param itemArr item数组
     */
    public void readItemState (int cid, Group group, Item[] itemArr) {
        OpcServerInfomation opcServerInfomation = OpcRegisterFactory.fetchOpcInfo(cid);
        Map<Item, ItemState> syncItems = null;
        try {
            syncItems = group.read(false, itemArr);
        } catch (JIException e) {
            log.error("Read item error.",e);
        }
        Long batchNum = originalDataRepository.gernateNextBatchNumber();
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
        List<IndicatorInstance> indicatorInstanceList = interfaceIndicatorDataGather(originalDataList);
        limitInstanceService.limitRealTimeCalculate(indicatorInstanceList);
    }

    /**
     * 接口指标数据汇集
     * @param originalDataList
     */
    public List<IndicatorInstance> interfaceIndicatorDataGather(List<OriginalData> originalDataList) {
        List<IndicatorInstance> indicatorInstanceList = new ArrayList<IndicatorInstance>();
        Map<String,OriginalData> originalDataMap = new HashMap<String,OriginalData>();
        List<String> codeList = new ArrayList<String>();
        for (OriginalData originalData:originalDataList) {
            MesuringPoint mesuringPoint = null;
            List<MesuringPoint> mesuringPointList = mesuringPointRepository.findAll(
                    Searchable.newSearchable()
                            .addSearchFilter("sourceCode", MatchType.EQ, originalData.getItemCode())).getContent();
            if (!mesuringPointList.isEmpty()) {
                codeList.add(mesuringPoint.getTargetCode());
                originalDataMap.put(mesuringPoint.getTargetCode(),originalData);
            }
        }

        List<IndicatorTemp> indicatorTempList = indicatorTempRepository.findAll(
                Searchable.newSearchable()
                        .addSearchFilter("itemCode", MatchType.IN, codeList)).getContent();
        for (IndicatorTemp indicatorTemp:indicatorTempList) {
            IndicatorInstance indicatorInstance = new IndicatorInstance(indicatorTemp);
            OriginalData originalData = originalDataMap.get(indicatorInstance.getIndicatorCode());
            indicatorInstance.setFloatValue(Double.parseDouble(originalData.getItemValue()));
            indicatorInstance.setValueType(IndicatorConsts.VALUE_TYPE_DOUBLE);
            Calendar cal = Calendar.getInstance();
            indicatorInstance.setInstanceTime(cal);
            indicatorInstance.setStatDateNum(Integer.parseInt(DateUtil.formatCalendar(cal, DateUtil.DATE_FORMAT_DAFAULT)));
            indicatorInstanceList.add(indicatorInstance);
        }
        indicatorInstanceRepository.save(indicatorInstanceList);
        return indicatorInstanceList;
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