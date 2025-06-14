package dreature.smit.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {
    // -----时间格式转化-----
    // Date 转化成格式化日期字符串
    public static String dateToFormatStr (Date date){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = secondFormat.format(date);
        return dateStr;
    }
    // Date 转化成格式化日期字符串
    public static String dateToFormatStr (Date date, String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String dateStr = format.format(date);
        return dateStr;
    }
    // 格式化日期字符串转化成 Date
    public static Date formatStrToDate (String dateStr) throws ParseException {
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = secondFormat.parse(dateStr);
        return date;
    }
    // 格式化日期字符串转化成 Date
    public static Date formatStrToDate (String dateStr, String formatStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = format.parse(dateStr);
        return date;
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(String timeStamp){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(Long.parseLong(timeStamp));
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(String timeStamp, String formatStr){
        SimpleDateFormat secondFormat = new SimpleDateFormat(formatStr);
        return secondFormat.format(Long.parseLong(timeStamp));
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(long timeStamp){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(timeStamp);
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(long timeStamp, String formatStr){
        SimpleDateFormat secondFormat = new SimpleDateFormat(formatStr);
        return secondFormat.format(timeStamp);
    }
    // -----时间获取-----
    // 获取当前时间戳（精确到毫秒）
    public static long getCurrentTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }
    // 获取当前的年月日时分秒（格式为 yyyy-MM-dd HH:mm:ss 的字符串）
    public static String getCurrentSecond() {
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(new Date());
    }
    // 获取当前的年月日（格式为 yyyy-MM-dd 的字符串）
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }
    // 获取当前的日（仅数值）
    public static Integer getCurrentD() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
    // 获取当前的年（格式为 yyyy-MM 的字符串）
    public static String getCurrentMonth(String sep) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        return monthFormat.format(new Date());
    }
    // 获取当前的月（仅数值）
    public static Integer getCurrentM() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }
    // 获取当前的年（格式为 yyyy 的字符串）
    public static String getCurrentYear() {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        return yearFormat.format(new Date());
    }
    // 获取当前的年（仅数值）
    public static Integer getCurrentY() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
    // 获取指定年月的当月第一天（格式为 yyyy-MM-dd 的字符串）
    public static String getFirstDayOfMonth(Integer year, Integer month){
        LocalDate localDate =LocalDate.of(year, month, 1);
        return localDate.with(TemporalAdjusters.firstDayOfMonth()).toString();

    }
    // 获取指定年月的当月最后一天（格式为 yyyy-MM-dd 的字符串）
    public static String getLastDayOfMonth(Integer year, Integer month){
        LocalDate localDate =LocalDate.of(year, month, 1);
        return localDate.with(TemporalAdjusters.lastDayOfMonth()).toString();

    }
    // 获得指定日当天的最晚时间（23:59:59）
    public static Date getEndTimeOfDate(Date date) {
        String defaultZone = "Asia/Shanghai";
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of(defaultZone));
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.of(defaultZone)).toInstant());
    }

    // 获得指定日当天的最早时间（00:00:00）
    public static Date getStartTimeOfDate(Date date) {
        String defaultZone = "Asia/Shanghai";
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of(defaultZone));
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.of(defaultZone)).toInstant());
    }
    public static Instant getEndOfDay(Instant date) {
        String defaultZone = "Asia/Shanghai";
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Date.from(date).getTime()), ZoneId.of(defaultZone));
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        Date from = Date.from(endOfDay.atZone(ZoneId.of(defaultZone)).toInstant());
        return from.toInstant();
    }
    // 获取起始时间至今，每天同一时刻的时间戳（列表）
    public static List<Long> getTimestampToNow(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, Integer nanoOfSecond) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Long> timestamps = new ArrayList<>();

        LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, minute, second, nanoOfSecond);

        LocalDateTime endTime = startTime;
        LocalDateTime now = LocalDateTime.now();

        while (endTime.isBefore(now) || endTime.isEqual(now)) {
            timestamps.add(endTime.toEpochSecond(ZoneOffset.ofHours(8)));
            endTime = endTime.plusDays(1L);
        }
        return timestamps;
    }
    // 获取起始日期至今的所有年月日（列表）
    public static List<String> getDatesToDate(Integer year, Integer month, Integer day) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> dates = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, day);
        LocalDate endDate = startDate;
        LocalDate today = LocalDate.now();

        while (endDate.isBefore(today) || endDate.isEqual(today)) {
            dates.add(endDate.format(dateFormat));
            endDate = endDate.plusDays(1L);
        }
        return dates;
    }
    // 获取起始日期至今的所有年月（列表）
    public static List<String> getMonthsToDate(Integer year, Integer month, Integer day) {

        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

        List<String> months = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate;
        LocalDate today = LocalDate.now();

        while (endDate.isBefore(today) || endDate.isEqual(today)) {
            months.add(endDate.format(monthFormat));
            endDate = endDate.plusMonths(1L);
        }
        return months;
    }
    // 获取起始日期至今的所有年（列表）
    public static List<String> getYearsToDate(Integer year, Integer month, Integer day) {

        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");

        List<String> years = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate;
        LocalDate today = LocalDate.now();

        while (endDate.isBefore(today) || endDate.isEqual(today)) {
            years.add(endDate.format(yearFormat));
            endDate = endDate.plusYears(1L);
        }
        return years;
    }
    // -----时间判断或基本运算-----
    // 是否是闰年
    public boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    // 日期1是否早于日期2（传入日期格式均为 yyyy-MM-dd）
    public static boolean isDate1BeforeDate2(String dateStr1, String dateStr2) {
        return dateStr1.compareTo(dateStr2) < 0;
    }
    // 日期1是否等于日期2（传入日期格式均为 yyyy-MM-dd）
    public static boolean isDate1EqualDate2(String dateStr1, String dateStr2) {
        return dateStr1.compareTo(dateStr2) == 0;
    }
    // 日期1是否晚于日期2（传入日期格式均为 yyyy-MM-dd）
    public static boolean isDate1AfterDate2(String dateStr1, String dateStr2) {
         return dateStr1.compareTo(dateStr2) > 0;
    }

}