package com.sq.protocol.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * UDP客户端，监听指定端口接收数据.
 * User: shuiqing
 * Date: 2015/7/30
 * Time: 10:51
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class UdpClient {

    private static final Logger log = LoggerFactory.getLogger(UdpClient.class);

    private static final int DATA_LENGTH = 1024*100;

    /**
     * 开启udp数据监听服务
     */
    public static void startLinsteningUdpService () {
        log.error("开启udp数据监听服务.");
        for (int cid=1;cid<= UdpSocketConfig.CLIENT_MAX;cid++) {
            UdpConnectInfo udpConnectInfo = UdpSocketConfig.udpConnectInfoMap.get(cid);
            UdpReceiverThread udpReceiverThread = new UdpReceiverThread();
            udpReceiverThread.setSysId(udpConnectInfo.getSysId());
            udpReceiverThread.setListening_port(udpConnectInfo.getListening_port());
            udpReceiverThread.start();
        }
    }
}