package com.sq.protocol.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket 服务组件，提供socket server服务，解析和保存client传递的报文数据..
 * User: shuiqing
 * Date: 2015/5/20
 * Time: 14:00
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class SocketServer extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    private static ServerSocket serverSocket;

    private Socket socket;

    public static ServerSocket getServerSocketSingleInstance () {
        try {
            if (null == serverSocket) {
                serverSocket = new ServerSocket(SocketConfig.SOCKET_PORT);
            }
        } catch (IOException e) {
            log.error("SocketServerComp->getServerSocketSingleInstances实例化ServerSocket出现错误.",e);
        }
        return serverSocket;
    }

    /**
     * 绑定host和port
     * @param host
     * @param port
     * @throws Exception
     */
    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 检测端口是否可用
     * @param port 端口
     * @return 端口是否可用
     */
    public static boolean isPortAvailable(int port) {
        Socket s = new Socket();
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *  接收客户端传来的报文数据，并解析
     */
    @Override
    public void run() {
        if (null == serverSocket) {
            serverSocket = getServerSocketSingleInstance();
        }

        log.error("Socket service on port->" + SocketConfig.SOCKET_PORT + ",Listening ws xml from client......");
        try{
            //每个请求交给一个线程去处理
            socket = serverSocket.accept();
            SocketDataDealTask socketDataDealTask = new SocketDataDealTask(socket);
            socketDataDealTask.start();
        }catch (Exception e){
            log.error("线程sleep出错，怎么可能！", e);
        }
    }
}
