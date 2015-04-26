package com.sq.protocol.ws.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * 请求 头对象
 * @author ShuiQing PM	
 * 2014年11月19日 下午1:40:06
 */
public class ReqHeader implements Serializable {

	private static final long serialVersionUID = 6308438592639609851L;

	private String authenCode;
	
	private String actionTime;

	public String getAuthenCode() {
		return authenCode;
	}

	@XmlElement(name = "authenCode")
	public void setAuthenCode(String authenCode) {
		this.authenCode = authenCode;
	}

	public String getActionTime() {
		return actionTime;
	}

	@XmlElement(name = "actionTime")
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
}
