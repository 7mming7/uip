package com.sq.protocol.socket;

import com.sq.comput.service.IndiComputService;
import com.sq.protocol.opc.domain.OriginalData;
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
import java.util.ArrayList;
import java.util.List;

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
        } catch (JAXBException e) {
            String msg = "解析报文失败: " + e.getMessage();
            log.error(msg, e);
        } catch (ParseException e) {
            String msg = "解析报文日期转换出错: " + e.getMessage();
            log.error(msg, e);
        }
    }
}
