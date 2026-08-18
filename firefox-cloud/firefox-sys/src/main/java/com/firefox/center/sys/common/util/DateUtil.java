package com.firefox.center.sys.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author: sujie
 * date: 2020-06-15
 */
public class DateUtil {

    private static final Integer MINUTES_DAY=60*24;
    private static final Integer MINUTES_HOUR=60;

    public static String DATE_FORMAT_Y="yyyy";
    public static String DATE_FORMAT_YM="yyyy-MM";
    public static String DATE_FORMAT_YMD="yyyy-MM-dd";
    public static String DATE_FORMAT_HM="HH:mm";
    public static String DATE_FORMAT_YMDHM="yyyy-MM-dd HH:mm";
    public static String DATE_FORMAT_YMDHMS="yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Long nowTimeStamp() {
        return System.currentTimeMillis()/1000L;
    }

    public static String getFormatDate(String formatStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(calendar.getTime());
    }

    public static String getFormatDate(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    public static Date getDate(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date unixTimestampToDate(String ts) {
        long lt = new Long(ts);
        return new Date(lt * 1000);
    }

    public static String unixTimestampFormat(String ts, String formatStr) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        long lt = new Long(ts);
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static Long getUnixTimestamp() {
        return new Date().getTime()/1000;
    }

    /**
     * date是否在startTime，endTime时间内
     * @param date
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isIn(Date date, Date startTime, Date endTime) {
        if ((date.getTime() >= startTime.getTime()) && date.getTime() < endTime.getTime() ) {
            return true;
        }
        return false;
    }

    public static boolean isInEq(Date date, Date startTime, Date endTime) {
        if ((date.getTime() >= startTime.getTime()) && date.getTime() < endTime.getTime() ) {
            return true;
        }
        return false;
    }

    /**
     * date是否在startTime， endTime前后timeInterval时间内
     * @param date
     * @param startTime
     * @param endTime
     * @param timeInterval
     * @return
     */
    public static boolean isInInterval(Date date, Date startTime, Date endTime, long timeInterval) {
        if ((startTime.getTime()-date.getTime()>=0 && startTime.getTime()-date.getTime()<timeInterval) ||
                (date.getTime()-endTime.getTime()>=0 && date.getTime()-endTime.getTime()<timeInterval)){
            return true;
        }
        return false;
    }

    public static boolean isBefore(Date date1, Date date2) {
        return date1.getTime()-date2.getTime()>=0?false:true;
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String getWeek(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("E");
        return formatter.format(date);
    }

    public static int getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        w=w==0?7:w;
        return w;
    }

    public static Date add(Date startTime, int type, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(type, interval);//mInterval分钟后的时间
        return calendar.getTime();
    }

    public static String add(int type, int interval, String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(type, interval);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String add(Date startTime, int type, int interval, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(type, interval);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getFirstDayOfMonth(String format) {
        Calendar cal = Calendar.getInstance();
        //获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        //设置日历中月份的最小天数
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH,firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getFirstDayOfMonth(int year, int month, String format) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH,firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static long getDiffDay(String startDateStr, String endDateStr){
        Date startDate=null;
        Date endDate=null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMD);
            startDate=sdf.parse(startDateStr);
            endDate=sdf.parse(endDateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return getDiff(startDate, endDate, 1000*60*60*24);
    }

    public static long getDiffHour(String startDateStr, String endDateStr){
        Date startDate=null;
        Date endDate=null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHMS);
            startDate=sdf.parse(startDateStr);
            endDate=sdf.parse(endDateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return getDiff(startDate, endDate, 1000*60*60);
    }

    public static long getDiff(Date startDate, Date endDate, long time){
        long n=0L;
        try{
            long start=startDate.getTime();
            long end=endDate.getTime();
            long Difference=end-start;//差值
            n=Difference/time;
        }catch (Exception e){
            e.printStackTrace();
        }
        return n;
    }

    public static String getMinuteStr(int parkTime){
        String timeStr="";
        int day=0,hour=0;
        if(parkTime>MINUTES_DAY){
            day=new Double(Math.ceil(parkTime/MINUTES_DAY)).intValue();
            timeStr+=day+"天";
            parkTime=parkTime-day*MINUTES_DAY;
            if(parkTime>MINUTES_HOUR){
                hour=new Double(Math.ceil(parkTime/MINUTES_HOUR)).intValue();
                timeStr+=hour+"小时";
                parkTime=parkTime-hour*MINUTES_HOUR;
            }
            if(parkTime!=0){
                timeStr+=parkTime+"分钟";
            }

        }else if(parkTime>MINUTES_HOUR){
            hour=new Double(Math.ceil(parkTime/MINUTES_HOUR)).intValue();
            timeStr+=hour+"小时";
            parkTime=parkTime-hour*MINUTES_HOUR;
            if(parkTime!=0){
                timeStr+=parkTime+"分钟";
            }
        }else{
            if(parkTime!=0){
                timeStr+=parkTime+"分钟";
            }
        }
        return timeStr;
    }




    public static void main(String[] args) {
        Date date1=getDate("2021-04-22 10:55", DATE_FORMAT_YMDHM);
        Date date2=getDate("2021-04-22 10:56", DATE_FORMAT_YMDHM);
        System.out.println(isBefore(date1, date2));
    }
}