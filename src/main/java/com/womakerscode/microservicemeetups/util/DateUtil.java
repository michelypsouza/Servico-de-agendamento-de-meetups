package com.womakerscode.microservicemeetups.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final String DATE_PATTERN_DEFAULT = "dd/MM/yyyy";
    public static final String DATE_TIME_PATTERN_DEFAULT = "dd/MM/yyyy HH:mm";

    public static String formatLocalDateTimeToStringWithOutTime(LocalDateTime localDateTime) {
        return formatPatternLocalDateTimeToString(localDateTime, DATE_PATTERN_DEFAULT);
    }

    public static String formatLocalDateTimeToStringWithTime(LocalDateTime localDateTime) {
        return formatPatternLocalDateTimeToString(localDateTime, DATE_TIME_PATTERN_DEFAULT);
    }

    public static LocalDateTime convertStringToLocalDateTimeWithOutTime(String date) {
        return convertPatternStringToLocalDateTime(date.toString(), DATE_PATTERN_DEFAULT);
    }

    public static LocalDateTime convertStringToLocalDateTimeWithTime(String date) {
        return convertPatternStringToLocalDateTime(date.toString(), DATE_TIME_PATTERN_DEFAULT);
    }

    private static String formatPatternLocalDateTimeToString(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("The date is invalid");
        }
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    private static LocalDateTime convertPatternStringToLocalDateTime(String date, String pattern) {
        return LocalDateTime.parse(date.toString(), DateTimeFormatter.ofPattern(pattern));
    }




//    public static String formatDateToString(Date date) {
//        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
//        format.setLenient(false);
//        return format.format(date);
//    }
//
//    public static Date getCurrentDate() {
//        return GregorianCalendar.getInstance(new Locale("pt", "BR")).getTime();
//    }
//
//    public static Date getDateWithZeroTime(int year, int month, int day) {
//        Calendar calendar = GregorianCalendar.getInstance(new Locale("pt", "BR"));
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month-1);
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        return getZeroTime(calendar);
//    }
//
//    private static Date getZeroTime(Calendar calendar) {
//        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
//        calendar.set(GregorianCalendar.MINUTE, 0);
//        calendar.set(GregorianCalendar.SECOND, 0);
//        calendar.set(GregorianCalendar.MILLISECOND, 0);
//        return calendar.getTime();
//    }

}
