package com.sq.protocol.ws.wsimpl.indicator;

import javax.jws.WebService;

/**
 * 指标计算web service
 * 作为server端
 * @author ShuiQing PM	
 * 2014年12月9日 下午5:04:11
 */
@WebService
public interface IWsServerIndicatorCompet {

	/**
	 * 接受指标重新计算的请求
	 * @param xmlStr 请求报文
	 * @return 响应报文
	 */
	public String receiveReComputIndicatorInfo(String xmlStr);
}
