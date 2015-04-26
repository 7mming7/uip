package com.sq.protocol.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 响应头信息
 * @author ShuiQing PM	
 * 2014年11月19日 下午4:34:37
 */
public class RpsHeader implements Serializable {

	private static final long serialVersionUID = 5935486088417311799L;

	private boolean success;
	
	private String remark;
	
	private String actionTime;

	public boolean isSuccess() {
		return success;
	}

	@XmlElement(name = "success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getRemark() {
		return remark;
	}

	@XmlElement(name = "remark")
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getActionTime() {
		return actionTime;
	}

	@XmlElement(name = "actionTime")
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
}
