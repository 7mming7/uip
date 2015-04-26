package com.sq.protocol.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 指标请求元素
 * @author ShuiQing PM	
 * 2014年11月19日 下午2:22:01
 */
@XmlRootElement(name = "indicator")
public class IndicatorReqElement{

	private String itemCode;
	
	private String instanceTime;

	public String getItemCode() {
		return itemCode;
	}

	@XmlElement(name = "itemCode")
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getInstanceTime() {
		return instanceTime;
	}

	@XmlElement(name = "instanceTime")
	public void setInstanceTime(String instanceTime) {
		this.instanceTime = instanceTime;
	}
}
