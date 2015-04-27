package com.sq.protocol.ws.wsimpl.indicator;

import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.service.IndiComputService;
import com.sq.comput.service.IndicatorTempService;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.entity.search.condition.OrCondition;
import com.sq.entity.search.condition.SearchFilterHelper;
import com.sq.exception.BaseException;
import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.ws.component.WsProtocalParser;
import com.sq.protocol.ws.domain.*;
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
	private IndiComputService indicatorComputService;

	@Autowired
	private IndicatorTempService indicatorTempService;
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public String receiveIndicatorCompetInfo(String xmlStr) {
		WsProtocalParser wsProtocalParser = WsProtocalParser.createInstance();
		
		MrpElementResponse<StandardResponse> mrpElementResponse = wsProtocalParser.createRpsMrpObject(true, null);
		String responseXml = "";
		StringWriter sw = new StringWriter();
        try {
            log.error("receiveIndicatorCompetInfo开始接收指标计算请求报文！开始时间："+new Date());
            log.error("请求收到同步时间---" + DateUtil.formatCalendar(Calendar.getInstance()));

            Calendar cal = DateUtil.stringToCalendar(xmlStr, DateUtil.DATE_FORMAT_DAFAULT);
            this.indicatorComputService.calculateDataGater(cal);
            
            sw = wsProtocalParser.beanToXml(mrpElementResponse, StandardResponse.class);
            responseXml = sw.toString();
            
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
		}
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public String receiveReComputIndicatorInfo(String xmlStr) {
		WsProtocalParser wsProtocalParser = WsProtocalParser.createInstance();

		MrpElementResponse<IndicatorReqElement> mrpElementResponse = wsProtocalParser.createRpsMrpObject(true, null);
		String responseXml = "";
		StringWriter sw = new StringWriter();
		try {
			log.error("receiveReComputIndicatorInfo开始接收指标重新计算请求报文！开始时间："+new Date());
			log.error("请求收到同步时间---" + DateUtil.formatCalendar(Calendar.getInstance()));

			MrpElementRequest<IndicatorReqElement> requestBean = WsProtocalParser.xmlToBean(xmlStr, new MrpElementRequest<IndicatorReqElement>(), IndicatorReqElement.class);

			/**
			 * 请求head
			 */
			ReqHeader reqHeader = requestBean.getReqHeader();

			/**
			 * 请求数据body
			 */
			List<IndicatorReqElement> indicatorEleList = requestBean.getAny();

			Searchable searchable = Searchable.newSearchable();
			Calendar cal = DateUtil.stringToCalendar(xmlStr, DateUtil.DATE_FORMAT_DAFAULT);
			List<IndicatorTemp> itemCodeList = new ArrayList<IndicatorTemp>();
			for (IndicatorReqElement ir : indicatorEleList) {
				OrCondition orCondition = (OrCondition) SearchFilterHelper.newCondition("indicatorCode", MatchType.EQ, "ir.getItemCode()");
				searchable.or(orCondition);
			}
			itemCodeList = indicatorTempService.findAll(searchable).getContent();
			indicatorComputService.reComputIndicator(cal,itemCodeList);

			sw = wsProtocalParser.beanToXml(mrpElementResponse, StandardResponse.class);
			responseXml = sw.toString();

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
		}
	}
}
