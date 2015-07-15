package com.sq.protocol.jodbc.service;

import com.sq.comput.domain.IndicatorInstance;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.jodbc.component.JodbcConnectHelper;
import com.sq.protocol.jodbc.domain.JodbcConsts;
import com.sq.protocol.jodbc.domain.Threshold;
import com.sq.protocol.jodbc.domain.Trade;
import com.sq.protocol.jodbc.repository.TradeDataRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.service.BaseService;
import com.sq.util.DateUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 地磅数据处理业务类.
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 13:58
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service
public class TradeService extends BaseService<Trade, Long> {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TradeService.class);

    @Autowired
    @BaseComponent
    private TradeDataRepository tradeDataRepository;

    @Autowired
    private OriginalDataRepository originalDataRepository;

    /**
     * 查询指标的增量数据
     */
    public void listTradesBySearchable(){
        Threshold threshold = tradeDataRepository.maxThreshold();
        ResultSet rsSync = null;
        Connection conn = JodbcConnectHelper.connect(JodbcConsts.DB_SQLSERVER);

        String sql = "select obj.* from Trade obj where obj.id > " + threshold.getLastMaxValue();
        try {
            Statement statement = conn.createStatement();
            rsSync = statement.executeQuery(sql);
            tradeDataSync(rsSync);
            pushTradeOrignalData(statement.executeQuery(sql));
        } catch (SQLException e) {
            log.error("listTradesBySearchable Sql执行出错.",e);
        }
        JodbcConnectHelper.releaseConn(conn);
    }


    /**
     * 初始化地磅历史数据
     */
    public void initializationTrades(){
        ResultSet rsSync = null;
        ResultSet rsPuch = null;
        Connection conn = JodbcConnectHelper.connect(JodbcConsts.DB_SQLSERVER);
        String sql = "select obj.* from Trade obj ";
        try {
            Statement statement = conn.createStatement();
            rsSync = statement.executeQuery(sql);
            rsPuch = statement.executeQuery(sql);
        } catch (SQLException e) {
            log.error("initializationTrades Sql执行出错.",e);
        }
        tradeDataSync(rsSync);
        pushTradeOrignalData(rsPuch);
        JodbcConnectHelper.releaseConn(conn);
    }

    /**
     * 地磅数据同步
     * @param rs
     */
    private void tradeDataSync(ResultSet rs){
        PreparedStatement preparedStatement = null;
        Connection connection =  JodbcConnectHelper.connect(JodbcConsts.DB_MYSQL);

        Long lastMaxValue = null;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO T_Trade ( ")
                .append("    ticketno1,ticketno2,station1,")
                .append("    station2,scaleno1,scaleno2, ")
                .append("    truckno,cardno,contractno, ")
                .append("    productcode,product,specification, ")
                .append("    sender,receiver,transporter, ")
                .append("    firstdatetime,seconddatetime,grossdatetime, ")
                .append("    taredatetime,firstweight,secondweight, ")
                .append("    gross,tare,net, ")
                .append("    productnet,exceptwater,exceptother, ")
                .append("    userid1,username1,userid2, ")
                .append("    username2,bc1,bc2, ")
                .append("    scaleweightflag,spareflag,uploadflag, ")
                .append("    dataeditflag,datastatus,manualinputflag, ")
                .append("    scalemode,finalflag,leftweight, ")
                .append("    tareweightalarmflag,taretimealarmflag, ")
                .append("    weighttimealarmflag,autosaveflag )")
                .append("  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String sql = sb.toString();
        try {
            boolean flag = false;
            preparedStatement = connection.prepareStatement(sql);
            while(rs.next()){
                flag = true;
                preparedStatement.setString(1, rs.getString("ticketno1"));
                preparedStatement.setString(2, rs.getString("ticketno2"));
                preparedStatement.setString(3, rs.getString("station1"));
                preparedStatement.setString(4, rs.getString("station2"));
                preparedStatement.setString(5, rs.getString("scaleno1"));
                preparedStatement.setString(6, rs.getString("scaleno2"));
                preparedStatement.setString(7, rs.getString("truckno"));
                preparedStatement.setString(8, rs.getString("cardno"));
                preparedStatement.setString(9, rs.getString("contractno"));
                preparedStatement.setString(10, rs.getString("productcode"));
                preparedStatement.setString(11, rs.getString("product"));
                preparedStatement.setString(12, rs.getString("specification"));
                preparedStatement.setString(13, rs.getString("sender"));
                preparedStatement.setString(14, rs.getString("receiver"));
                preparedStatement.setString(15, rs.getString("transporter"));
                preparedStatement.setString(16, rs.getString("firstdatetime"));
                preparedStatement.setString(17, rs.getString("seconddatetime"));
                preparedStatement.setString(18, rs.getString("grossdatetime"));
                preparedStatement.setString(19, rs.getString("taredatetime"));

                preparedStatement.setString(20, rs.getString("firstweight"));
                preparedStatement.setString(21, rs.getString("secondweight"));
                preparedStatement.setString(22, rs.getString("gross"));
                preparedStatement.setString(23, rs.getString("tare"));
                preparedStatement.setString(24, rs.getString("net"));
                preparedStatement.setString(25, rs.getString("productnet"));
                preparedStatement.setString(26, rs.getString("exceptwater"));
                preparedStatement.setString(27, rs.getString("exceptother"));
                preparedStatement.setString(28, rs.getString("userid1"));
                preparedStatement.setString(29, rs.getString("username1"));
                preparedStatement.setString(30, rs.getString("userid2"));
                preparedStatement.setString(31, rs.getString("username2"));
                preparedStatement.setString(32, rs.getString("bc1"));
                preparedStatement.setString(33, rs.getString("bc2"));
                preparedStatement.setString(34, rs.getString("scaleweightflag"));
                preparedStatement.setString(35, rs.getString("spareflag"));
                preparedStatement.setString(36, rs.getString("uploadflag"));
                preparedStatement.setString(37, rs.getString("dataeditflag"));
                preparedStatement.setString(38, rs.getString("datastatus"));
                preparedStatement.setString(39, rs.getString("manualinputflag"));
                preparedStatement.setString(40, rs.getString("scalemode"));
                preparedStatement.setString(41, rs.getString("finalflag"));
                preparedStatement.setString(42, rs.getString("leftweight"));
                preparedStatement.setString(43, rs.getString("tareweightalarmflag"));
                preparedStatement.setString(44, rs.getString("taretimealarmflag"));
                preparedStatement.setString(45, rs.getString("weighttimealarmflag"));
                preparedStatement.setString(46, rs.getString("autosaveflag"));
                lastMaxValue = rs.getLong("id");
                preparedStatement.addBatch();
            }
            if (flag) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
                sql = "INSERT INTO T_Threshold (lastUpdateTime ,lastMaxValue ) VALUES (now()," + lastMaxValue + ")";
                preparedStatement.execute(sql);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("JDBC SQL执行出错.",e);
        }
    }

    /**
     * 推送地磅数据到数据原始表中
     * @param rs
     */
    private void pushTradeOrignalData(ResultSet rs){
        PreparedStatement preparedStatement = null;
        Connection connection =  JodbcConnectHelper.connect(JodbcConsts.DB_MYSQL);

        Long nextBatchNum = originalDataRepository.gernateNextBatchNumber(JodbcConsts.SYS_ODBC_LOADOMETER);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO t_originaldata ( ")
                .append("    batchNum,instanceTime,itemCode,")
                .append("    itemValue,sysId )")
                .append("  VALUES(?,?,?,?,?)");
        String sql = sb.toString();
        try {
            preparedStatement = connection.prepareStatement(sql);
            while(rs.next()){
                preparedStatement.setLong(1, nextBatchNum);
                preparedStatement.setString(2, rs.getString("seconddatetime"));
                preparedStatement.setString(3, rs.getString("productcode"));
                preparedStatement.setString(4, rs.getString("productnet"));
                preparedStatement.setInt(5, JodbcConsts.SYS_ODBC_LOADOMETER);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("JDBC SQL执行出错.",e);
        }
    }
}
