package com.sq.protocol.socket;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/8/7
 * Time: 15:22
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class UdpConnectInfo {

    public int c_id;

    public int sysId;

    public int listening_port;

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

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
}
