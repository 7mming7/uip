package com.sq.protocol.socket;

import com.sq.protocol.opc.component.OpcRegisterFactory;
import com.sq.protocol.opc.service.MesuringPointService;
import com.sq.protocol.opc.service.PushDataThirdService;
import com.sq.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * UDP接收数据处理线程.
 * User: shuiqing
 * Date: 2015/8/7
 * Time: 15:48
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class UdpReceiverThread extends Thread {

    private static Logger log = LoggerFactory.getLogger(UdpReceiverThread.class);

    private static final int TIMEOUT = 0;  //设置接收数据的超时时间

    private static final int DATA_LENGTH = 1024*1000;

    /**
     * 由于Thread非spring启动时实例化，而是根据具体的逻辑动态实例化，所以需要通过此方式从spring的context中获取相应的bean.
     */
    private MesuringPointService mesuringPointService = SpringUtils.getBean(MesuringPointService.class);

    private PushDataThirdService pushDataThirdService = SpringUtils.getBean(PushDataThirdService.class);

    public int sysId;

    public int listening_port;

    public int getSysId() {
        return sysId;
    }

    public void setSysId(int sysId) {
        this.sysId = sysId;
    }

    public int getListening_port() {
        return listening_port;
    }

    public void setListening_port(int listening_port) {
        this.listening_port = listening_port;
    }

    @Override
    public void run() {
        byte[] buf = new byte[DATA_LENGTH];
        try {
            //客户端在端口监听接收到的数据
            DatagramSocket ds = new DatagramSocket(listening_port);
            InetAddress loc = InetAddress.getLocalHost();

            //定义用来接收数据的DatagramPacket实例
            DatagramPacket dp_receive = new DatagramPacket(buf, DATA_LENGTH);
            //数据发向本地端口
            ds.setSoTimeout(TIMEOUT);     //设置接收数据时阻塞的最长时间

            boolean connFlag = true;     //是否接收到数据的标志位

            int i = 1;
            while(connFlag){

                Thread.sleep(1000l);

                //接收从服务端发送回来的数据
                ds.receive(dp_receive);
                /*log.error("dp_receive:" + new String(dp_receive.getData(), 0, dp_receive.getLength()));*/
                syncPointCache(new String(dp_receive.getData(), 0, dp_receive.getLength()));

                if (i % 60 == 0) {
                    mesuringPointService.receiveUdpDataMysql(sysId, new String(dp_receive.getData(), 0, dp_receive.getLength()));
                }

                i++;

                //如果收到数据，则打印出来
                String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +
                        " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
                //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
                //所以这里要将dp_receive的内部消息长度重新置为1024
                dp_receive.setLength(DATA_LENGTH);
            }
            ds.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch(InterruptedIOException e){
            log.error("获取监听端口的数据失败.",e);
        } catch (IOException e) {
            log.error("数据通讯异常.", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步更新测点的缓存数据
     * @param receiveMsg
     */
    public static void syncPointCache(String receiveMsg){
        String[] pointEntityArray = receiveMsg.split(";");
        for (String pointStr:pointEntityArray) {
            String[] paramArray = pointStr.split("%%");
            OpcRegisterFactory.mesuringPointCacheMap.put(paramArray[0], paramArray[1]);
            /*log.info("pointStr:" + pointStr);
            log.info("receive data-- itemCode:" + paramArray[0] + ",itemValue:" + paramArray[1]);*/
        }
    }
}
