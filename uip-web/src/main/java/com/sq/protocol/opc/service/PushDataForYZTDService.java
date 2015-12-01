package com.sq.protocol.opc.service;

import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.component.UtgardOpcHelper;
import com.sq.protocol.opc.domain.OpcServerInfomation;
import com.sq.protocol.opc.domain.ScreenInfo;
import com.sq.protocol.opc.repository.ScreenInfoRepository;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.openscada.opc.lib.da.browser.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 扬州泰达首页烟气数据推送
 * User: shuiqing
 * Date: 15/11/13
 * Time: 下午2:19
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class PushDataForYZTDService {


    private static final Logger log = LoggerFactory.getLogger(PushDataForYZTDService.class);

    private static final int DCS_CID = 1;

    @Autowired
    @BaseComponent
    private ScreenInfoRepository screenInfoRepository;

    DecimalFormat format = new DecimalFormat("0.00");

    private static final int TIMEOUT = 0;  //设置接收数据的超时时间

    private static final int DATA_LENGTH = 1024*100;

    private static Server server;

    private static Group group;

    private static Item[] itemArr;

    private static List<ScreenPushData> screenPushDataList= new ArrayList<ScreenPushData>();

    /**
     * 初始化item数组
     */
    private void initItemArr() {
        server = UtgardOpcHelper.connect(DCS_CID);
        Collection<Leaf> leafs = new LinkedList<Leaf>();
        screenPushDataList = fillCodeList();
        for (ScreenPushData screenPushData:screenPushDataList) {
            Leaf leaf = new Leaf(null, screenPushData.getItemCode(), screenPushData.getItemCode());
            leafs.add(leaf);
        }
        itemArr = new Item[leafs.size()];
        int item_flag = 0;
        try {
            group = server.addGroup();
            for (Leaf leaf : leafs) {
                log.debug("code->" + leaf.getItemId());
                Item item = group.addItem(leaf.getItemId());
                itemArr[item_flag] = item;
                item_flag++;
            }
        }  catch (UnknownHostException e) {
            log.error("Host unknow error.", e);
        } catch (NotConnectedException e) {
            log.error("Connnect to opc error.", e);
        } catch (JIException e) {
            log.error("Opc server connect error.", e);
        } catch (DuplicateGroupException e) {
            log.error("Group duplicate error.", e);
        } catch (AddFailedException e) {
            log.error("Group add error.", e);
        }
    }

    /**
     * 大屏数据推送
     */
    public void screenDataPush () {
        try {
            if (itemArr == null) {
                initItemArr();
            }
            if(group == null) {
                log.error("group is null!");
            }
            Map<Item, ItemState> syncItems = null;
            syncItems = group.read(true, itemArr);
            List<ScreenInfo> screenInfoList = screenInfoRepository.findAll();
            for (Map.Entry<Item, ItemState> entry : syncItems.entrySet()) {
                log.debug("key= " + entry.getKey().getId()
                        + " and value= " + entry.getValue().getValue().toString());
                String itemValue = entry.getValue().getValue().toString();
                for (ScreenPushData screenPushData:screenPushDataList) {
                    if (screenPushData.getItemCode().equals(entry.getKey().getId())) {
                        screenPushData.setItemValue(
                                format.format(new BigDecimal(itemValue.substring(2, itemValue.length() - 2))));
                    }
                }
            }

            pushScreenDataSync(screenInfoList,screenPushDataList);
            screenInfoRepository.save(screenInfoList);
        } catch (JIException e) {
            log.error("Opc server connect error.", e);
        }
    }

    /**
     * 组织大屏显示需要的测点列表
     * @return
     */
    public List<ScreenPushData> fillCodeList () {
        List<ScreenPushData> screenPushDataList = new ArrayList<ScreenPushData>();

        //DUTY
        screenPushDataList.add(generateScreenPushData("DUTY->1->AW7002/FC2001AI:AI010706.PNT"));
        screenPushDataList.add(generateScreenPushData("DUTY->2->AW7002/FC1001AI:AI010706.PNT"));
        screenPushDataList.add(generateScreenPushData("DUTY->3->AW7002/FC3001AI:AI018803.PNT"));

        //HCL
        screenPushDataList.add(generateScreenPushData("HCL->1->AW7002/FC2001AI:AI010604.PNT"));
        screenPushDataList.add(generateScreenPushData("HCL->2->AW7002/FC1001AI:AI010604.PNT"));
        screenPushDataList.add(generateScreenPushData("HCL->3->AW7002/FC3001AI:AI018702.PNT"));

        //CO
        screenPushDataList.add(generateScreenPushData("CO->1->AW7002/FC2001AI:AI010605.PNT"));
        screenPushDataList.add(generateScreenPushData("CO->2->AW7002/FC3001AI:AI018702.PNT"));
        screenPushDataList.add(generateScreenPushData("CO->3->AW7002/FC3001AI:AI018703.PNT"));

        //SO2
        screenPushDataList.add(generateScreenPushData("SO2->1->AW7002/FC2001AI:AI010607.PNT"));
        screenPushDataList.add(generateScreenPushData("SO2->2->AW7002/FC1001AI:AI010607.PNT"));
        screenPushDataList.add(generateScreenPushData("SO2->3->AW7002/FC3001AI:AI018705.PNT"));

        //NOX
        screenPushDataList.add(generateScreenPushData("NOX->1->AW7002/FC2001AI:AI010606.PNT"));
        screenPushDataList.add(generateScreenPushData("NOX->2->AW7002/FC1001AI:AI010606.PNT"));
        screenPushDataList.add(generateScreenPushData("NOX->3->AW7002/FC3001AI:AI018704.PNT"));

        //主蒸汽流量
        screenPushDataList.add(generateScreenPushData("O2->1->AW7002/FC2001AI:AI010705.PNT"));
        screenPushDataList.add(generateScreenPushData("O2->2->AW7002/FC1001AI:AI010705.PNT"));
        screenPushDataList.add(generateScreenPushData("O2->3->AW7002/FC3001AI:AI018802.PNT"));

        return screenPushDataList;
    }

    /**
     * 根据字符串生成点值对象
     * @param screenCode
     * @return
     */
    private ScreenPushData generateScreenPushData (String screenCode) {
        String[] strArr = screenCode.split("->");
        ScreenPushData screenPushData = new ScreenPushData();
        screenPushData.setGroup(strArr[0]);
        screenPushData.setSerialNo(Integer.parseInt(strArr[1]));
        screenPushData.setItemCode(strArr[2]);
        return screenPushData;
    }

    /**
     * 同步大屏数据
     * @param screenInfoList
     * @param screenPushDataList
     * @return
     */
    private static List<ScreenInfo> pushScreenDataSync(List<ScreenInfo> screenInfoList, List<ScreenPushData> screenPushDataList) {
        boolean flagGl1 = true;
        boolean flagGl2 = true;
        boolean flagGl3 = true;

        for (ScreenPushData screenPushData:screenPushDataList) {
            if (screenPushData.getGroup().equals("ZQ")) {
                log.debug("#:group:" + screenPushData.getGroup() + ";code:" + screenPushData.getItemCode() + "value:" + Double.parseDouble(screenPushData.getItemValue()));
                if (screenPushData.getSerialNo() == 1 &&  Double.parseDouble(screenPushData.getItemValue()) <= 20) {
                    flagGl1 = false;
                } else if (screenPushData.getSerialNo() == 2 &&  Double.parseDouble(screenPushData.getItemValue()) <= 20) {
                    flagGl2 = false;
                } else if (screenPushData.getSerialNo() == 3 &&  Double.parseDouble(screenPushData.getItemValue()) <= 20) {
                    flagGl3 = false;
                }
            }
        }

        for (ScreenInfo screenInfo:screenInfoList) {
            if (screenInfo.getBianma().equals("ZHUANGTAI")) {
                if(!flagGl1) {
                    screenInfo.setGuolu1("检修");
                } else {
                    screenInfo.setGuolu1("运行");
                }
                if(!flagGl2) {
                    screenInfo.setGuolu2("检修");
                } else {
                    screenInfo.setGuolu2("运行");
                }
                if(!flagGl3) {
                    screenInfo.setGuolu3("检修");
                } else {
                    screenInfo.setGuolu3("运行");
                }
            }
        }

        /*for (ScreenInfo screenInfo:screenInfoList) {
            if (screenInfo.getBianma().equals("ZHUANGTAI")) {
                if(screenInfo.getGuolu1().equals("检修")) {
                    flagGl1 = false;
                } else if (screenInfo.getGuolu2().equals("检修")){
                    flagGl2 = false;
                } else if (screenInfo.getGuolu3().equals("检修")){
                    flagGl3 = false;
                } else if (screenInfo.getGuolu4().equals("检修")){
                    flagGl4 = false;
                }
            }
        }*/

        for (ScreenPushData screenPushData:screenPushDataList) {
            for (ScreenInfo screenInfo:screenInfoList) {
                if (screenInfo.getBianma().equals(screenPushData.getGroup())) {
                    if (screenPushData.getSerialNo() == 1) {
                        if (flagGl1) {
                            screenInfo.setGuolu1(screenPushData.getItemValue());
                        } else {
                            screenInfo.setGuolu1("检修");
                        }
                    } else if (screenPushData.getSerialNo() == 2) {
                        if (flagGl2) {
                            screenInfo.setGuolu2(screenPushData.getItemValue());
                        } else {
                            screenInfo.setGuolu2("检修");
                        }
                    } else if (screenPushData.getSerialNo() == 3) {
                        if (flagGl3) {
                            screenInfo.setGuolu3(screenPushData.getItemValue());
                        } else {
                            screenInfo.setGuolu3("检修");
                        }
                    }
                }
            }
        }

        return screenInfoList;
    }

    /**
     * 测点和测值的对应关系
     */
    class ScreenPushData{

        private String group;

        private String itemCode;

        private String itemValue;

        private Integer serialNo;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Integer getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(Integer serialNo) {
            this.serialNo = serialNo;
        }
    }
}
