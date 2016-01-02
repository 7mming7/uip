package com.sq.quota.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 指标计算配置
 * User: shuiqing
 * Date: 15/11/9
 * Time: 下午1:59
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class QuotaBaseConfigure {

    private static final Logger log = LoggerFactory.getLogger(QuotaBaseConfigure.class);

    private static Properties prop;

    /** 配置文件路径 */
    private final static String CONFIG_FILE_NAME = "/conf/indicator_calculate_conf.properties";

    /** 一天定义的起始和结束的时间 */
    public static String CONFIG_DAY_HOUR = "DAY_HOUR";

    //开始的小时数
    public static int startHour;

    //结束的小时数
    public static int endHour;

    static {
        loadConfigProperties();
    }

    /**
     * 加载指标计算服务的配置文件
     * @return
     */
    private static Properties loadConfigProperties () {
        prop = new Properties();
        try {
            prop.load(QuotaBaseConfigure.class.getResourceAsStream(CONFIG_FILE_NAME));
            String day_hour = getEntryValue(CONFIG_DAY_HOUR);
            String[] hourArr = day_hour.split("-");
            startHour = Integer.parseInt(hourArr[0]);
            endHour = Integer.parseInt(hourArr[1]);
        } catch (IOException e) {
            log.error("Quota calculate 加载" + CONFIG_FILE_NAME + "配置文件出错.", e);
        }
        return prop;
    }

    /**
     * 通过名字获得配置的值
     *
     * @param name
     * @return
     */
    public static String getEntryValue(String name) {
        return prop.getProperty(name);
    }

}
