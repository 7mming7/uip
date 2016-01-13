package com.sq.protocol.ws.wsimpl.indicator;

import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.OrCondition;
import com.sq.entity.search.condition.SearchFilterHelper;
import com.sq.exception.BaseException;
import com.sq.protocol.ws.component.WsProtocalParser;
import com.sq.protocol.ws.domain.*;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.service.QuotaComputInsService;
import com.sq.quota.service.QuotaTempService;
import com.sq.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 指标计算ws服务端实现
 * @author ShuiQing PM	
 * 2014年12月9日 下午5:17:52
 */
@Service
@Qualifier("iWsServerIndicatorCompet")
@WebService(endpointInterface = "com.sq.protocol.ws.wsimpl.indicator.IWsServerIndicatorCompet", serviceName = "IWsServerIndicatorCompet")
public class WsServerIndiCompet4Standard implements IWsServerIndicatorCompet{

    private static Logger log = LoggerFactory.getLogger(WsServerIndiCompet4Standard.class);

    @Autowired
    private QuotaComputInsService quotaComputInsService;

    @Autowired
    private QuotaTempService quotaTempService;

    /**
     * 接受指标重新计算
     * @param xmlStr 请求报文
     * @return
     */
    @SuppressWarnings({ "unchecked", "static-access" })
    @Override
    public String receiveReComputIndicatorInfo(String xmlStr) {
        WsProtocalParser wsProtocalParser = WsProtocalParser.createInstance();
        MrpElementResponse<IndicatorReqElement> mrpElementResponse = wsProtocalParser.createRpsMrpObject(true, null);
        String responseXml = "";
        StringWriter sw = new StringWriter();
        try {
            log.error("- - - - - - - - - - - - - - - - - - - - - - -");
            log.error("receiveReComputIndicatorInfo开始接收指标重新计算请求报文！当前时间：" + DateUtil.formatCalendar(Calendar.getInstance()));
            log.error("收到的报文--- " + xmlStr);

            MrpElementRequest<IndicatorReqElement> requestBean = WsProtocalParser.xmlToBean(xmlStr, new MrpElementRequest<IndicatorReqElement>(), IndicatorReqElement.class);

            /**
             * 请求head
             */
            ReqHeader reqHeader = requestBean.getReqHeader();
            log.error("指标计算日期：" + reqHeader.getActionTime());

            /**
             * 请求数据body
             */
            List<IndicatorReqElement> indicatorEleList = requestBean.getAny();

            if (!indicatorEleList.isEmpty()) {
                Searchable searchable = Searchable.newSearchable();
                Calendar cal = DateUtil.stringToCalendar(reqHeader.getActionTime(), DateUtil.DATE_FORMAT_DAFAULT);
                List<QuotaTemp> itemCodeList = new ArrayList<QuotaTemp>();
                OrCondition orCondition = new OrCondition();
                log.error("需要进行关联计算的基础指标项：");
                for (IndicatorReqElement ir : indicatorEleList) {
                    log.error("     --- IndicatorReqElement-> " + ir.getItemCode());
                    orCondition.add(SearchFilterHelper.newCondition("indicatorCode", MatchType.EQ, ir.getItemCode()));
                }
                searchable.or(orCondition);
                itemCodeList = quotaTempService.findAll(searchable).getContent();
                quotaComputInsService.reComputQuota(cal, itemCodeList);
            }

            sw = wsProtocalParser.beanToXml(mrpElementResponse, StandardResponse.class);
            responseXml = sw.toString();

            log.error("计算完成：" + responseXml);
            return responseXml;
        } catch (BaseException e) {
            String msg = "解析报文失败: " + e.getMessage();
            log.error(msg, e);
            mrpElementResponse.getRpsHeader().setSuccess(false);
            mrpElementResponse.getRpsHeader().setRemark(msg);
            try {
                sw = WsProtocalParser.beanToXml(mrpElementResponse, IndicatorRpsElement.class);
            } catch (FileNotFoundException | JAXBException e1) {
                String msgEx = "解析报文失败: " + e1.getMessage();
                log.error(msgEx, e1);
            }
            return sw.toString();
        } catch (JAXBException e) {
            String msg = "解析报文失败: " + e.getMessage();
            log.error(msg, e);
            mrpElementResponse.getRpsHeader().setSuccess(false);
            mrpElementResponse.getRpsHeader().setRemark(msg);
            try {
                sw = WsProtocalParser.beanToXml(mrpElementResponse, IndicatorRpsElement.class);
            } catch (FileNotFoundException | JAXBException e1) {
                String msgEx = "解析报文失败: " + e1.getMessage();
                log.error(msgEx, e1);
            }
            return sw.toString();
        } catch (FileNotFoundException e) {
            String msg = "bean2XML转化出错: " + e.getMessage();
            log.error(msg, e);
            mrpElementResponse.getRpsHeader().setSuccess(false);
            mrpElementResponse.getRpsHeader().setRemark(msg);
            try {
                sw = WsProtocalParser.beanToXml(mrpElementResponse, IndicatorRpsElement.class);
            } catch (FileNotFoundException | JAXBException e1) {
                String msgEx = "bean2XML转化出错: " + e1.getMessage();
                log.error(msgEx, e1);
            }
            return sw.toString();
        } catch (ParseException e) {
            String msg = "时间转化出现错误。";
            log.error(msg, e);
            mrpElementResponse.getRpsHeader().setSuccess(false);
            mrpElementResponse.getRpsHeader().setRemark(msg);
            try {
                sw = WsProtocalParser.beanToXml(mrpElementResponse, IndicatorRpsElement.class);
            } catch (FileNotFoundException | JAXBException e1) {
                String msgEx = "bean2XML转化出错: " + e1.getMessage();
                log.error(msgEx, e1);
            }
            return sw.toString();
        } catch (Exception e) {
            String msg = "reComput出现错误。";
            log.error(msg, e);
            mrpElementResponse.getRpsHeader().setSuccess(false);
            mrpElementResponse.getRpsHeader().setRemark(msg);
            try {
                sw = WsProtocalParser.beanToXml(mrpElementResponse, IndicatorRpsElement.class);
            } catch (FileNotFoundException | JAXBException e1) {
                String msgEx = "bean2XML转化出错: " + e1.getMessage();
                log.error(msgEx, e1);
            }
            return sw.toString();
        }
    }
}
