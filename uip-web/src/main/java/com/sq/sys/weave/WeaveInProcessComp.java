package com.sq.sys.weave;

import com.sq.protocol.socket.UdpClient;
import com.sq.quota.service.QuotaComputInsService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * system init之后,执行自定义的方法.
 * User: shuiqing
 * Date: 2015/5/20
 * Time: 15:35
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class WeaveInProcessComp implements BeanPostProcessor {


    /**
     * bean实例化之前的操作
     * 参看父类中的注释 @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    /**
     * bean实例化之后的操作
     * 参看父类中的注释 @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        if (bean instanceof UdpClient) {
            /*((UdpClient) bean).startLinsteningUdpService();*///开启socket服务
        } else if (bean instanceof QuotaComputInsService) {
            /*((QuotaComputInsService) bean).init();*///重新加载指标的native表达式
        }
        return bean;
    }
}
