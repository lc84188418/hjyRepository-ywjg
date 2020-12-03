package com.hjy.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 */
public class DateUtil {

    public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";

    public static final String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String formatFullTime(LocalDateTime localDateTime) {
        return formatFullTime(localDateTime, FULL_TIME_PATTERN);
    }

    public static String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    private static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simformat = new SimpleDateFormat(dateFormatType);
        return simformat.format(date);
    }

    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }
    public static Date addTime(Date date,int num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,num);
        date = calendar.getTime();
        return date;
    }
    public static String translateLong(Long time){
        StringBuffer stringBuffer = new StringBuffer();
        long hour = 0;
        long min = 0;
        long second = 0;
         hour = time/3600;
         min = (time%3600)/60;
         second = time%3600%60;
        stringBuffer.append(hour+"时"+min+"分"+second+"秒");
        return stringBuffer.toString();
    }
}
