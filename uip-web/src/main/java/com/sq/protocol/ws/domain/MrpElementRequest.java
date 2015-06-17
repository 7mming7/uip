package com.sq.protocol.ws.domain;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * controller service基础请求抽象模型
 * @author ShuiQing PM	
 * 2014年11月19日 下午12:05:56
 * @param <T>
 */
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class MrpElementRequest<T> implements Serializable{

	private static final long serialVersionUID = -8284690859554620664L;

	/**
	 * 请求体
	 */
	@XmlElement(name = "head")
	private ReqHeader reqHeader;
	
	/**
	 * 请求数据元素集合
	 */
	@XmlAnyElement(lax = true)
	@XmlElementWrapper(name="datas")
	private List<T> any;

	public ReqHeader getReqHeader() {
		return reqHeader;
	}

	public void setReqHeader(ReqHeader reqHeader) {
		this.reqHeader = reqHeader;
	}

	public List<T> getAny() {
		return any;
	}

	public void setAny(List<T> any) {
		this.any = any;
	}
}
