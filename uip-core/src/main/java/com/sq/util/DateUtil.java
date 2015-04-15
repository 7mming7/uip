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

    public static final String DATE_FORMAT_YYMM = "yyMM";

    /**
     * 默认日期类型格式.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DAFAULT);

    public static String formatDate(Date d) {
        if (d == null)
        {
            return "";
        }
        return dateFormat.format(d);
    }


    /**
     * 格式化整数型日期
     * @param intDate
     * @return
     */
    public static String formatIntDate(Integer intDate) {
        if (intDate == null)
        {
            return "";
        }
        Calendar c = newCalendar(intDate);
        return formatCalendar(c);
    }

    /**
     * 根据指定格式化来格式日期.
     * 2014年1月26日 下午3:44:23 shuiqing添加此方法
     * @param date
     *            待格式化的日期.
     * @param pattern
     *            格式化样式或分格,如yyMMddHHmmss
     * @return 字符串型日期.
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null)
        {
            return "";
        }
        if (StringUtils.isBlank(pattern))
        {
            return formatDate(date);
        }
        SimpleDateFormat simpleDateFormat = null;
        try
        {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } catch (Exception e)
        {
            e.printStackTrace();
            return formatDate(date);
        }
        return simpleDateFormat.format(date);
    }


    /**
     * 根据整型日期生成Calendar
     * 2014年1月26日 下午3:44:00 shuiqing添加此方法
     * @param date
     * @return
     */
    public static Calendar newCalendar(int date) {
        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        Calendar ret = Calendar.getInstance();
        ret.set(year, month - 1, day);
        return ret;
    }

    /**
     * 格式化Calendar
     * 2014年1月26日 下午3:44:00 shuiqing添加此方法
     * @param calendar
     * @return
     */
    public static String formatCalendar(Calendar calendar) {
        if (calendar == null)
        {
            return "";
        }
        return dateFormat.format(calendar.getTime());
    }

    public static String formatCalendar(Calendar calendar, SimpleDateFormat df) {
        if (calendar == null)
        {
            return "";
        }
        return df.format(calendar.getTime());
    }

    /**
     * 将java.util.Date类型转换成java.util.Calendar类型
     *
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cdate = Calendar.getInstance();
        cdate.setTime(date);
        return cdate;
    }

    /**
     * 指定日期的月第一天
     * 2014年2月13日 下午7:57:15 shuiqing添加此方法
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_MONTH, 1);
        return cdate.getTime();
    }

    /**
     * 计算指定日期的月最后一天
     * 2014年2月13日 下午7:45:21 shuiqing添加此方法
     * @param date1
     * @return
     */
    public static Date getLastDayOfMonth(Date date1) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, 1);
        date.add(Calendar.DAY_OF_YEAR, -1);
        return date.getTime();
    }

    public static Date getLastDayOfMonth(Calendar date) {
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, 1);
        date.add(Calendar.DAY_OF_YEAR, -1);
        return date.getTime();
    }

    /**
     * 计算两个int日期之间所有的日期
     * 2014年1月26日 下午3:02:39 shuiqing添加此方法
     * @param startDate
     * @param endDate
     * @return
     */
    @SuppressWarnings("static-access")
    public static List<String> dayListBetweenDate (int startDate, int endDate) {
        if (startDate == 0 || endDate == 0)
            return null;
        List<String> dayList = new LinkedList<String>();
        Calendar startCar = newCalendar(startDate);
        Calendar endCar = newCalendar(endDate);
        while (!startCar.getTime().equals(endCar.getTime())) {
            dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));
            startCar.add(startCar.DATE, 1);
        }
        dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));

        return dayList;
    }

    /**
     * 计算两个Date日期之间所有的日期
     * 2014年5月13日 下午5:19:08 ShuiQing添加此方法
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> dayListBetweenDate (Date startDate, Date endDate) {
        List<String> dayList = new LinkedList<String>();
        Calendar startCar = dateToCalendar(startDate);
        Calendar endCar = dateToCalendar(endDate);
        while (!startCar.getTime().equals(endCar.getTime())) {
            dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));
            startCar.add(startCar.DATE, 1);
        }
        dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));

        return dayList;
    }

    /**
     * 计算一个日期所在月份的所有的天数
     * 2014年5月13日 下午5:20:41 ShuiQing添加此方法
     * @param currentDate 当前时间
     * @return
     */
    public static List<String> dayListByDate (Date currentDate) {
        Date startDate = getMonthStart(currentDate);
        Date endDate = getLastDayOfMonth(currentDate);
        List<String> dayList = new LinkedList<String>();
        Calendar startCar = dateToCalendar(startDate);
        Calendar endCar = dateToCalendar(endDate);
        while (!startCar.getTime().equals(endCar.getTime())) {
            dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));
            startCar.add(startCar.DATE, 1);
        }
        dayList.add(formatDate(startCar.getTime(), DATE_FORMAT_DAFAULT));

        return dayList;
    }

    /**
     * 计算两个int日期之间所有的月份
     * 2014年2月13日 下午4:12:57 shuiqing添加此方法
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> dayMonthListBetweenDate (int startDate, int endDate) {
        if (startDate == 0 || endDate == 0)
            return null;
        List<Date> monthList = new LinkedList<Date>();
        Calendar startCar = newCalendar(startDate);
        Calendar endCar = newCalendar(endDate);
        endCar.set(endCar.DATE, 1);
        while (startCar.before(endCar)) {
            monthList.add(startCar.getTime());
            startCar.add(startCar.MONTH, 1);
        }
        monthList.add(startCar.getTime());
        return monthList;
    }

    /**
     * 计算两个int日期之间所有的月份
     * 2014年2月13日 下午4:12:57 shuiqing添加此方法
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> monthListBetweenDate (int startDate, int endDate) {
        if (startDate == 0 || endDate == 0)
            return null;
        List<String> monthList = new LinkedList<String>();
        Calendar startCar = newCalendar(startDate);
        Calendar endCar = newCalendar(endDate);
        endCar.set(endCar.DATE, 1);
        while (startCar.before(endCar)) {
            monthList.add(formatDate(startCar.getTime(), DATE_FORMAT_MONTH));
            startCar.add(startCar.MONTH, 1);
        }
        monthList.add(formatDate(startCar.getTime(), DATE_FORMAT_MONTH));
        return monthList;
    }

    /**
     * 月份的首尾
     * 2014年2月13日 下午8:08:52 shuiqing添加此方法
     * @param monthList
     * @return
     */
    public static List<String> dayMonthListBetweenDate (List<Date> monthList) {
        List<String> monthDayList = new LinkedList<String>();
        for (Date date : monthList) {
            Date firstDay = getMonthStart(date);
            monthDayList.add(formatDate(firstDay, DATE_FORMAT_DAFAULT));
            Date lastDay = getLastDayOfMonth(date);
            monthDayList.add(formatDate(lastDay, DATE_FORMAT_DAFAULT));
        }
        return monthDayList;
    }

    /**
     * 计算两个日期之间的天数
     * 2014年2月25日 下午4:53:55 shuiqing添加此方法
     * @param firstDate 第一个日期
     * @param SecondDate 第二个日期
     * @return 日期间间隔的天数
     */
    public static long dayCountBetweenTwoDate(Date firstDate, Date SecondDate){
        return (SecondDate.getTime()-firstDate.getTime())/1000/60/60/24;
    }

    /**
     * 格式化字符串
     * 2014年5月7日 上午9:47:50 Shuiqing添加此方法
     * @param date
     * @return
     */
    public static String parseDateToFormatString(Date date) {
        return dateFormat.format(date);
    }

    /**
     * 把String字符串转换成Calendar
     */
    public static Calendar stringToCalendar(String dateTime,String format) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat(format);
        Date date =sdf.parse(dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}