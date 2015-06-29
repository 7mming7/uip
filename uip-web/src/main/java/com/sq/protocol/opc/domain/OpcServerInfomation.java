package com.sq.protocol.opc.domain;

import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.Leaf;

import java.util.Collection;

/**
 * OPC服务信息，主要包含Group以及Item.
 * User: shuiqing
 * Date: 2015/4/4
 * Time: 16:58
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class OpcServerInfomation {

    private int c_id;

    private boolean conn_status;

    private String sysId;

    private Server server;

    /** 连接信息 */
    private ConnectionInformation connectionInformation;

    /** 叶子节点 */
    private Collection<Leaf> leafs;

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public ConnectionInformation getConnectionInformation() {
        return connectionInformation;
    }

    public void setConnectionInformation(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    public Collection<Leaf> getLeafs() {
        return leafs;
    }

    public void setLeafs(Collection<Leaf> leafs) {
        this.leafs = leafs;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public boolean isConn_status() {
        return conn_status;
    }

    public void setConn_status(boolean conn_status) {
        this.conn_status = conn_status;
    }
}
