package com.sq.protocol.ws.component;

import com.sq.protocol.ws.domain.*;
import com.sq.util.DateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 接口报文解析器
 * @author ShuiQing PM	
 * 2014年11月19日 下午2:14:47
 */
public class WsProtocalParser {

	private static final WsProtocalParser instance = new WsProtocalParser();
    
    private static final String ENCODING = "UTF-8";
    
    public static WsProtocalParser createInstance() {
        return instance;
    }
	
	/**
	 * 将XML转化为javabean对象
	 * 2014年11月19日 下午2:15:35 ShuiQing PM 添加此方法
	 * @param xml 请求XML文本
	 * @param t 泛型化待转化的javabean对象
	 * @return 转化后的javabean对象
	 * @throws javax.xml.bind.JAXBException
	 */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String xml, T t, Class<?> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(t.getClass(), clazz);
        Unmarshaller um = context.createUnmarshaller();
        StringReader sr = new StringReader(xml);
        t = (T) um.unmarshal(sr);
        return t;
    }
    
    /**
     * 将javabean对象转化为XML
     * 2014年11月19日 下午4:22:02 ShuiQing PM 添加此方法
     * @param t 泛型化javabean对象
     * @return 转化后的XML文本
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.FileNotFoundException
     */
    public static <T> StringWriter beanToXml(T t,Class<?> clazz) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(t.getClass(), clazz);
        Marshaller m = context.createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(t, sw);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化
        /*m.marshal(t, new FileOutputStream("src/main/java/com/sn//core/ws/indicatorTest.xml"));*/
        return sw;
    }
    
    /**
     * 创建初始化响应对象
     * 2014年11月19日 下午5:16:48 ShuiQing PM 添加此方法
     * @param success 成功标识
     * @param remark 备注
     * @return 初始化响应对象
     */
    public MrpElementResponse createRpsMrpObject(boolean success, String remark) {
    	MrpElementResponse response = new MrpElementResponse();
                
    	RpsHeader rpsHeader = new RpsHeader();
        
        rpsHeader.setSuccess(success);
        rpsHeader.setRemark(remark);
        rpsHeader.setActionTime(DateUtil.formatCalendar(Calendar.getInstance(), DateUtil.DATE_FORMAT_YMDHMS));
        
        response.setRpsHeader(rpsHeader);
        
        return response;
    }
    
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
    	ReqHeader reqHeader = new ReqHeader();
    	reqHeader.setActionTime(DateUtil.formatDate(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_YMDHMS));
    	reqHeader.setAuthenCode("snmis");
    	
    	IndicatorReqElement indicatorReqElement = new IndicatorReqElement();
    	indicatorReqElement.setInstanceTime("2014:14:12");
    	indicatorReqElement.setItemCode("fdsfds");
    	
    	List<IndicatorReqElement> indicatorReqElementList = new ArrayList<IndicatorReqElement>();
    	indicatorReqElementList.add(indicatorReqElement);
    	/*IndicatorReqBody indicatorReqBody = new IndicatorReqBody();
    	HashSet<IndicatorReqElement> indicatorReqElementSet = new HashSet<IndicatorReqElement>();
    	indicatorReqElementSet.add(indicatorReqElement);
    	indicatorReqBody.setIndicatorReqElementSet(indicatorReqElementSet);*/
    	
    	MrpElementRequest<IndicatorReqElement> mrpElementRequest = new MrpElementRequest<IndicatorReqElement>();
    	mrpElementRequest.setReqHeader(reqHeader);
    	mrpElementRequest.setAny(indicatorReqElementList);
    	
    	StringWriter sw = beanToXml(mrpElementRequest,IndicatorReqElement.class);
        System.out.println(sw.toString());
        
        MrpElementRequest<IndicatorReqElement> xmlToBean = xmlToBean(sw.toString(), mrpElementRequest,IndicatorReqElement.class);
        System.out.println(xmlToBean.toString());
	}
}
