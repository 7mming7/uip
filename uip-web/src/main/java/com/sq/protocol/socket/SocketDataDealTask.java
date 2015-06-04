package com.sq.protocol.socket;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.comput.domain.IndicatorInstance;
import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.repository.IndicatorInstanceRepository;
import com.sq.comput.repository.IndicatorTempRepository;
import com.sq.comput.service.LimitInstanceService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.protocol.opc.domain.MesuringPoint;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.protocol.ws.component.WsProtocalParser;
import com.sq.protocol.ws.domain.IndicatorRpsElement;
import com.sq.protocol.ws.domain.MrpElementResponse;
import com.sq.util.DateUtil;
import com.sq.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

/**
 * Socket方式下接收client数据并做处理.
 * User: shuiqing
 * Date: 2015/5/20
 * Time: 11:41
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class SocketDataDealTask extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SocketDataDealTask.class);

    /**
     * 由于Thread非spring启动时实例化，而是根据具体的逻辑动态实例化，所以需要通过此方式从spring的context中获取相应的bean.
     */
    private OriginalDataRepository originalDataRepository = SpringUtils.getBean(OriginalDataRepository.class);

    private static MesuringPointRepository mesuringPointRepository = SpringUtils.getBean(MesuringPointRepository.class);

    private static IndicatorTempRepository indicatorTempRepository = SpringUtils.getBean(IndicatorTempRepository.class);

    private static IndicatorInstanceRepository indicatorInstanceRepository = SpringUtils.getBean(IndicatorInstanceRepository.class);

    private static LimitInstanceService limitInstanceService = SpringUtils.getBean(LimitInstanceService.class);

    public Socket sk = null;

    public SocketDataDealTask(Socket sk){
        this.sk = sk;
    }

    @Override
    public void run() {
        try {
            PrintWriter wtr = new PrintWriter(sk.getOutputStream());
            BufferedReader rdr = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            while (rdr.readLine() != null) {
                String line = rdr.readLine();
                log.error("Data come from client：" + line);
                if (!line.contains("<?xml version=")) {
                    continue;
                }
                int start = line.indexOf('<');
                int last = line.lastIndexOf('>');
                String responseXml = line.substring(start, last + 1);
                saveOriginalData(responseXml);
            }
            wtr.flush();
            Thread.sleep(100);
        } catch (IOException e) {
            String msg = "服务端接收数据出现错误！";
            log.error(msg, e);
        } catch (InterruptedException e) {
            log.error("SocketDataDealTask Thread sleep 出现错误.", e);
        }
    }

    /**
     * 保存报文中传来的实时指标数据
     * 2015年2月3日 下午4:44:13 ShuiQing PM 添加此方法
     * @param responseXml 响应报文
     */
    public void saveOriginalData (String responseXml) {
        try {
            List<OriginalData> originalDataList = new ArrayList<OriginalData>();
            MrpElementResponse<IndicatorRpsElement> responseBean = WsProtocalParser.xmlToBean(responseXml,
                    new MrpElementResponse<IndicatorRpsElement>(), IndicatorRpsElement.class);
            List<IndicatorRpsElement> indiEleList = responseBean.getAny();
            long current = System.currentTimeMillis();
            for (IndicatorRpsElement indicatorRpsElement : indiEleList) {
                OriginalData originalData = new OriginalData();
                originalData.setInstanceTime(
                        DateUtil.stringToCalendar(indicatorRpsElement.getInstanceTime(), DateUtil.DATE_FORMAT_YMDHMS));
                originalData.setItemCode(indicatorRpsElement.getItemCode());
                originalData.setItemValue(indicatorRpsElement.getItemValue());
                originalData.setBatchNum(current);
                originalDataList.add(originalData);
            }
            originalDataRepository.save(originalDataList);
            List<IndicatorInstance> indicatorInstanceList = interfaceIndicatorDataGather(originalDataList);
            limitInstanceService.limitRealTimeCalculate(indicatorInstanceList);
        } catch (JAXBException e) {
            String msg = "解析报文失败: " + e.getMessage();
            log.error(msg, e);
        } catch (ParseException e) {
            String msg = "解析报文日期转换出错: " + e.getMessage();
            log.error(msg, e);
        }
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
            indicatorInstance.setStatDateNum(Integer.parseInt(DateUtil.formatCalendar(cal,DateUtil.DATE_FORMAT_DAFAULT)));
            indicatorInstanceList.add(indicatorInstance);
        }
        indicatorInstanceRepository.save(indicatorInstanceList);
        return indicatorInstanceList;
    }
}
