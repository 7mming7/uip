package com.sq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 时间相关util.
 * User: shuiqing
 * Date: 2015/4/15
 * Time: 16:49
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class DateUtil {

    public static final String DATE_FORMAT_DAFAULT = "yyyyMMdd";

    public static final String DATE_FORMAT_MONTH = "yyyyMM";

    public static final String DATE_FORMAT_Y_M_D = "yyyy-MM-dd";

    public static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_DAFAULTYMDHMS = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT_YYMM = "yyMM";

    public static final String DATE_FORMAT_YY_MM = "yy-MM";

    public static final String DATE_FORMAT_MM_DD = "MM-dd";

    public static final String DATE_FORMAT_MMDD = "MMdd";

    public static final String DATE_FORMAT_DD = "dd";

    /**
     * 默认日期类型格式.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DAFAULT);

    /**
     * 格式化日期为yyyyMMdd格式
     * @param date 需要格式化的日期
     * @return 返回格式化后的字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormat.format(date);
    }

    /**
     * 根据指定格式化来格式日期.
     * 2014年1月26日 下午3:44:23 shuiqing添加此方法
     * @param date 待格式化的日期,如date为空，则返回空字符串
     * @param pattern 格式化样式或分格,如yyMMddHHmmss。如该参数为空或者不符合要求，则使用默认的yyyyMMdd格式进行转换
     * @return 字符串型日期.
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isBlank(pattern)) {
            return formatDate(date);
        }
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return formatDate(date);
        }
        return simpleDateFormat.format(date);
    }

    /**
     * 根据整型日期生成Calendar
     * @param intDate 整形格式的日期；如20140124
     * @return
     */
    public static Calendar intDate2Calendar(int intDate) {
        int year = intDate / 10000;
        int month = (intDate % 10000) / 100;
        int day = intDate % 100;

        Calendar ret = Calendar.getInstance();
        ret.set(year, month - 1, day);
        return ret;
    }

    /**
     * 格式化Calendar,返回默认的日期格式字符串
     * 本方法不指定format，采用默认的format，DateUtil.DATE_FORMAT_DAFAULT；如果需要制定format，
     * 请调用formatCalendar(Calendar calendar,String fomart)
     * @param calendar 需要格式化的日历对象
     * @return
     */
    public static String formatCalendar(Calendar calendar) {
        return formatCalendar(calendar, DATE_FORMAT_DAFAULT);
    }

    /**
     * 格式化Calendar,返回指定的日期格式字符串
     * @param calendar 需要格式化的日历对象
     * @param fomart 格式化日历格式
     * @return
     */
    public static String formatCalendar(Calendar calendar,String fomart) {
        if (calendar == null) {
            return "";
        }
        return formatDate(calendar.getTime(), fomart);
    }

    /**
     * 将java.util.Date类型转换成java.util.Calendar类型
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cdate = Calendar.getInstance();
        cdate.setTime(date);
        return cdate;
    }

    /**
     * 指定日期月份的第一天
     * 2014年2月13日 下午7:57:15 shuiqing添加此方法
     * @param date
     * @return
     */
    public static Date getStartDayOfMonth(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_MONTH, 1);
        return cdate.getTime();
    }

    /**
     * 计算指定日期的月最后一天
     * 2014年2月13日 下午7:45:21 shuiqing添加此方法
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * 计算两个int日期之间所有的日期
     * 2014年1月26日 下午3:02:39 shuiqing添加此方法
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> dayListBetweenDate (int startDate, int endDate) {
        if (startDate == 0 || endDate == 0) {
            return null;
        }
        if (startDate > endDate) {
            return null;
        }
        List<String> dayList = new LinkedList<String>();
        Calendar startCar = intDate2Calendar(startDate);
        Calendar endCar = intDate2Calendar(endDate);
        while (!startCar.getTime().equals(endCar.getTime())) {
            dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));
            startCar.add(Calendar.DATE, 1);
        }
        dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));

        return dayList;
    }

    /**
     * 计算一个日期所在月份的所有的天数
     * @param currentDate 当前时间
     * @return
     */
    public static List<String> getAllDaysOfMonth (Date currentDate) {
        Date startDate = getStartDayOfMonth(currentDate);
        Date endDate = getLastDayOfMonth(currentDate);
        List<String> dayList = new LinkedList<String>();
        Calendar startCar = dateToCalendar(startDate);
        Calendar endCar = dateToCalendar(endDate);
        while (!startCar.getTime().equals(endCar.getTime())) {
            dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));
            startCar.add(Calendar.DATE, 1);
        }
        dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));

        return dayList;
    }

    /**
     * 把字符串转成Date
     * @param formt  指定的格式
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strDate, String formt) throws ParseException{
        SimpleDateFormat sdf= new SimpleDateFormat(formt);
        Date date = sdf.parse(strDate);
        return date;
    }

    /**
     * 把String字符串转换成Calendar
     * @param dateTime 要转化的数据
     * @param format 数据的格式
     * @return
     * @throws ParseException
     */
    public static Calendar stringToCalendar(String dateTime, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 将日期时间从一种格式转换为另一种格式
     * @param srcTime 源串
     * @param srcPattern    源串格式
     * @param destPattern 目标串格式
     * @return String 目标串
     */
    public static String transDateTime(String srcTime, String srcPattern, String destPattern) {
        if (srcTime == null) {
            return "";
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat();
            fmt.applyPattern(srcPattern);
            Date date = fmt.parse(srcTime);
            fmt.applyPattern(destPattern);
            return fmt.format(date);
        } catch (Exception exp) {
        }
        return srcTime;
    }

    public static String getLastDayOfCurrentMonth(String sdf){
        //获取当前月最后一天
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return formatCalendar(calendar, sdf);
    }

    /**
     * 计算两个时间相差的天数
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 获得系统当前时间
     * @param format
     * @return
     */
    public static String getCurrDateTime(String format) {
        String currTimeStr;
        try {
            java.util.Date currDate = new java.util.Date();
            SimpleDateFormat dtFmt = new SimpleDateFormat(format);
            currTimeStr = dtFmt.format(currDate);
        } catch (Exception e) {
            return "";
        }
        return currTimeStr;
    }
}