package com.sq.protocol.socket;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.domain.OriginalData;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.protocol.ws.component.WsProtocalParser;
import com.sq.protocol.ws.domain.IndicatorRpsElement;
import com.sq.protocol.ws.domain.MrpElementResponse;
import com.sq.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * socket服务端.
 * User: shuiqing
 * Date: 2015/5/19
 * Time: 13:49
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class SocketServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    @Autowired
    @BaseComponent
    private OriginalDataRepository originalDataRepository;

    private static Properties prop;

    private static ServerSocket server = null;
    private Socket sk = null;
    private BufferedReader rdr = null;
    private PrintWriter wtr = null;

    static {
        init();
    }

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/socket-config.properties";

    /** socket服务的端口号 */
    public final static String CONFIG_SOCKET_PORT = "socket_port";

    /** 配置在配置文件中的SOCKET的端口号 */
    public static int SOCKET_PORT;

    public SocketServer(){

    }

    public static ServerSocket init(){
        try{
            if (server != null) {
                return null;
            }
            //加载socket配置文件
            loadConfigProperties();
            server = new ServerSocket(SOCKET_PORT);
        }catch (IOException e){
            log.error("新建一个服务端的连接！", e);
        }
        return server;
    }

    /**
     * 加载socket服务的配置文件
     * @return
     */
    private static Properties loadConfigProperties () {
        prop = new Properties();
        try {
            prop.load(SocketServer.class.getResourceAsStream(CONFIG_FILE_NAME));
            SOCKET_PORT = Integer.parseInt(getEntryValue(CONFIG_SOCKET_PORT));
        } catch (IOException e) {
            log.error("Socket conf 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    public void run() {
        while (true) {
            log.error("Listening......");
            try{
                Thread.sleep(100000);
                //每个请求交给一个线程去处理
                sk = server.accept();
                ServerThread th = new ServerThread(sk);
                th.start();
                Thread.sleep(1000);
            }catch (Exception e){
                String msg = "线程sleep出错，怎么可能！";
                log.error(msg, e);
            }
        }
    }

    /**
     * 内部socketserver线程
     * @author ShuiQing PM
     * 2015年1月22日 下午8:43:30
     */
    public class ServerThread extends Thread{
        Socket sk = null;
        public ServerThread(Socket sk){
            this.sk = sk;
        }
        public void run(){
            try{
                wtr = new PrintWriter(sk.getOutputStream());
                rdr = new BufferedReader(new InputStreamReader(sk.getInputStream()));
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
            }catch (IOException e){
                String msg = "服务端接收数据出现错误！";
                log.error(msg, e);
            }
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
            System.out.println(responseXml);
            MrpElementResponse<IndicatorRpsElement> responseBean = WsProtocalParser.xmlToBean(responseXml,
                    new MrpElementResponse<IndicatorRpsElement>(), IndicatorRpsElement.class);
            System.out.println(responseXml);
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

    /**
     * 通过名字获得配置的值
     *
     * @param name
     * @return
     */
    public static String getEntryValue(String name) {
        return prop.getProperty(name);
    }
}
