package com.sq.protocol.ws.domain;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * controller service基础响应抽象模型
 * @author ShuiQing PM	
 * 2014年11月19日 下午4:31:50
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class MrpElementResponse<T> implements Serializable{

	private static final long serialVersionUID = -4405417237311381201L;

	/**
	 * 请求体
	 */
	@XmlElement(name = "head")
	private RpsHeader rpsHeader;
	
	/**
	 * 响应数据元素集合
	 */
	@XmlAnyElement(lax = true)
	@XmlElementWrapper(name="datas")
	private List<T> any;

	public RpsHeader getRpsHeader() {
		return rpsHeader;
	}

	public void setRpsHeader(RpsHeader rpsHeader) {
		this.rpsHeader = rpsHeader;
	}

	public List<T> getAny() {
		return any;
	}

	public void setAny(List<T> any) {
		this.any = any;
	}
}
