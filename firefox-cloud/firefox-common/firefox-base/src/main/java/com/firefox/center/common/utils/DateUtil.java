package com.firefox.center.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 * 
 * @Author: sujie
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
    public static String DATE_FORMAT_HMS="HH:mm:ss";
    public static final String DATE_FORMAT_ZH = "yyyy年MM月dd日";
    public static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Long nowTimeStamp() {
        return System.currentTimeMillis()/1000L;
    }

    public static Date unixTimestampToDate(String ts) {
        long lt = new Long(ts);
        return new Date(lt * 1000);
    }

    public static String format(String formatStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(calendar.getTime());
    }

    public static String format(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    public static Date format(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String unixTimestampFormat(String ts, String formatStr) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        long lt = new Long(ts);
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static Long unixTimestamp() {
        return new Date().getTime()/1000L;
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

    public static String week(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("E");
        return formatter.format(date);
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

    public static String firstDayOfMonth(String format) {
        Calendar cal = Calendar.getInstance();
        //获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH,firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String firstDayOfMonth(int year, int month, String format) {
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

    public static long diffDay(String startDateStr, String endDateStr){
        Date startDate=null;
        Date endDate=null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMD);
            startDate=sdf.parse(startDateStr);
            endDate=sdf.parse(endDateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return diff(startDate, endDate, 1000*60*60*24);
    }

    public static long diffHour(String startDateStr, String endDateStr){
        Date startDate=null;
        Date endDate=null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHMS);
            startDate=sdf.parse(startDateStr);
            endDate=sdf.parse(endDateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return diff(startDate, endDate, 1000*60*60);
    }

    public static long diff(Date startDate, Date endDate, long time){
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

    public static String minuteStr(int parkTime){
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

    public static Date getDate(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH,day);
        return cal.getTime();
    }

    public static String getDateYMD() {
        return getDateYMD(new Date());
    }

    public static String getDateYMD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMD);
        return sdf.format(new Date());
    }

    public static Date getWeekStart() {
        Calendar cal=Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH),0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    public static Date getWeekEnd() {
        Calendar cal=Calendar.getInstance();
        cal.setTime(getWeekStart());
        cal.add(Calendar.DAY_OF_WEEK,7);
        return cal.getTime();
    }

    public static Date getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    public static Date getSeasonStart(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        if(month == 1 || month ==2 || month ==3){
            cal.set(cal.get(Calendar.YEAR), 0, minDay, 00, 00, 00);
        }else if(month == 4 || month ==5 || month ==6){
            cal.set(cal.get(Calendar.YEAR), 3, minDay, 00, 00, 00);
        }else if(month == 7 || month ==8 || month ==9){
            cal.set(cal.get(Calendar.YEAR), 6, minDay, 00, 00, 00);
        }else if(month == 10 || month ==11 || month ==12){
            cal.set(cal.get(Calendar.YEAR), 9, minDay, 00, 00, 00);
        }
        return cal.getTime();
    }

    public static Date getSeasonEnd(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        if(month == 1 || month ==2 || month ==3){
            cal.set(cal.get(Calendar.YEAR), 3, minDay, 00, 00, 00);
        }else if(month == 4 || month ==5 || month ==6){
            cal.set(cal.get(Calendar.YEAR), 6, minDay, 00, 00, 00);
        }else if(month == 7 || month ==8 || month ==9){
            cal.set(cal.get(Calendar.YEAR), 9, minDay, 00, 00, 00);
        }else if(month == 10 || month ==11 || month ==12){
            cal.set(cal.get(Calendar.YEAR)+1, 0, minDay, 00, 00, 00);
        }
        return cal.getTime();
    }

    public static Date getYearStart(){
        Calendar cal = Calendar.getInstance();
        int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(cal.get(Calendar.YEAR), 0, minDay, 00, 00, 00);
        return cal.getTime();
    }

    public static Date getYearEnd(){
        Calendar cal = Calendar.getInstance();
        int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(cal.get(Calendar.YEAR)+1, 0, minDay, 00, 00, 00);
        return cal.getTime();
    }

    /**
     * 取得日期：年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 取得日期：月
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month + 1;
    }

    /**
     * 取得日期：年
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int da = c.get(Calendar.DAY_OF_MONTH);
        return da;
    }

    /**
     * 取得当天日期是周几
     *
     * @param date
     * @return
     */
    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekOfYear = c.get(Calendar.DAY_OF_WEEK);
        return weekOfYear - 1;
    }

    /**
     * 取得一年的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        return weekOfYear;
    }

    /**
     * getWeekBeginAndEndDate
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getWeekBeginAndEndDate(Date date, String pattern) {
        Date monday = getMondayOfWeek(date);
        Date sunday = getSundayOfWeek(date);
        return formatDate(monday, pattern) + " - "
                + formatDate(sunday, pattern);
    }

    /**
     * 根据日期取得对应周周一日期
     *
     * @param date
     * @return
     */
    public static Date getMondayOfWeek(Date date) {
        Calendar monday = Calendar.getInstance();
        monday.setTime(date);
        monday.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return monday.getTime();
    }

    /**
     * 根据日期取得对应周周日日期
     *
     * @param date
     * @return
     */
    public static Date getSundayOfWeek(Date date) {
        Calendar sunday = Calendar.getInstance();
        sunday.setTime(date);
        sunday.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return sunday.getTime();
    }

    /**
     * 取得月的剩余天数
     *
     * @param date
     * @return
     */
    public static int getRemainDayOfMonth(Date date) {
        int dayOfMonth = getDayOfMonth(date);
        int day = getPassDayOfMonth(date);
        return dayOfMonth - day;
    }

    /**
     * 取得月已经过的天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得月天数
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * 取得月最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     *
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    /**
     * 获取季度英文
     * @param date
     * @return
     */
    public static String getSeasonEn(Date date) {
        int season = getSeason(date);
        switch (season) {
            case 1:
                return  "1ST";
            case 2:
                return  "2ND";
            case 3:
                return  "3RD";
            case 4:
                return  "4TH";
            default:
                return  "1ST";
        }
    }

    /**
     * 返回加/减几年
     * @param date
     * @return
     */
    public static Date addDate(Date date, Integer year, Integer mouth, Integer day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(null != year){
            cal.add(Calendar.YEAR, year);
        }
        if(null != mouth){
            cal.add(Calendar.MONTH, mouth);
        }
        if(null != day){
            cal.add(Calendar.DATE, day);
        }
        return cal.getTime();
    }

    /**
     * 获取一天中开始的时间
     * @param date
     * @return
     */
    public static Date get00Time(Date date){
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c.setTime(date);
        c2.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 00, 00, 00);
        return c2.getTime();
    }

    /**
     * 获取当天最后一刻
     * @param date
     * @return
     */
    public static Date get23Time(Date date){
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c.setTime(date);
        c2.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return c2.getTime();
    }

    /**
     * 判断是否成年
     * @param date
     * @return
     */
    public static Boolean isAdult(Date date){
        Date oldDate = addDate(new Date(), 18, null, null);
        return date.before(oldDate);
    }

    /**
     * 转为时间戳
     * @param time
     * @return
     */
    public static long parseTime(String time){
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YMDHMS);
        long target = 0L;
        try {
            target = format.parse(time).getTime()/1000L;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return target;
    }

    /**
     * 判断时间是否在一个区间内
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * parseDate
     * @param strDate
     * @return
     */
    public static Date parseDate(String strDate) {
        return parseDate(strDate, null);
    }

    /**
     * parseDate
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date parseDate(String strDate, String pattern) {
        Date date = null;
        try {
            if (pattern == null) {
                pattern = DATE_FORMAT_YMD;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            date = format.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * format date
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    /**
     * format date
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        String strDate = null;
        try {
            if (pattern == null) {
                pattern = DATE_FORMAT_YMD;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            strDate = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 判断时间是否在一个区间内
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveDate(String startTime, String endTime) {
        return isEffectiveDate(new Date(), parseDate(startTime, DATE_FORMAT_YMDHMS), parseDate(endTime, DATE_FORMAT_YMDHMS));
    }

    /**
     * 几秒之前
     * @param startTime
     * @param endTime
     * @return
     */
    public static long aFewSecondsBefore(String startTime, String endTime){
        return aFewSecondsBefore(parseDate(startTime, DATE_FORMAT_YMDHMS), parseDate(endTime, DATE_FORMAT_YMDHMS));
    }

    /**
     * 几秒之前
     * @param date
     * @return
     */
    public static long aFewSecondsBefore(String date){
        return aFewSecondsBefore(parseDate(date, DATE_FORMAT_YMDHMS));
    }

    /**
     * 几秒之前
     * @param date
     * @return
     */
    public static long aFewSecondsBefore(Date date){
        return aFewSecondsBefore(date, new Date());
    }

    /**
     * 几秒之前
     * @param startDate
     * @param endDate
     * @return
     */
    public static long aFewSecondsBefore(Date startDate, Date endDate){
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * 几秒之前转为秒
     * @param startDate
     * @param endDate
     * @return
     */
    public static int aFewSecondsBeforeToSeconds(Date startDate, Date endDate){
        return longToDate(aFewSecondsBefore(startDate, endDate)).getSeconds();
    }

    /**
     * 几秒之前转为秒
     * @param date
     * @return
     */
    public static int aFewSecondsBeforeToSeconds(Date date){
        return longToDate(aFewSecondsBefore(new Date())).getSeconds();
    }

    /**
     * 几秒之前转为秒
     * @param date
     * @return
     */
    public static int aFewSecondsBeforeToSeconds(String date){
        return longToDate(aFewSecondsBefore(date)).getSeconds();
    }

    /**
     * long转date
     * @param dateLong
     * @return
     */
    public static Date longToDate(long dateLong){
        Date date = new Date(dateLong);
        return date;
    }

    /**
     * date转long
     * @param date
     * @return
     */
    public static long dateToLong(Date date){
        long dateLong = date.getTime();
        return  dateLong;
    }




    public static void main(String[] args) {
        System.out.println(diffHour("2020-12-29 14:50:00", "2020-12-29 15:55:00"));
    }
}
