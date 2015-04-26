package com.sq.protocol.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 指标数据响应元素
 * @author ShuiQing PM	
 * 2014年11月19日 下午7:15:01
 */
@XmlRootElement(name = "indicator")
public class IndicatorRpsElement {

	private String itemCode;
	
	private String itemValue;
	
	private String instanceTime;
	
	public String getItemCode() {
		return itemCode;
	}

	@XmlElement(name="itemCode")
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemValue() {
		return itemValue;
	}

	@XmlElement(name="itemValue")
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getInstanceTime() {
		return instanceTime;
	}

	@XmlElement(name="instanceTime")
	public void setInstanceTime(String instanceTime) {
		this.instanceTime = instanceTime;
	}
}
