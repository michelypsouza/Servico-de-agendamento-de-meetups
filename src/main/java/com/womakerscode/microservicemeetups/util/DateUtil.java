package com.womakerscode.microservicemeetups.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    //public static final String DATE_TIME_PATTERN_DEFAULT = "dd-MM-yyyy HH:mm:ss";
    //public static final String DATE_TIME_PATTERN_DEFAULT = "yyyy-MM-dd'T'HH:mm";
    //public static final String DATE_TIME_FULL_PATTERN = "dd/MM/yyyy'T'HH:mm:ss.SSSXXX'['VV']'";
    public static final String DATE_TIME_PATTERN_DEFAULT = "dd/MM/yyyy HH:mm";

    public static String formatLocalDateTimeToStringWithTime(LocalDateTime localDateTime) {
        return formatPatternLocalDateTimeToString(localDateTime, DATE_TIME_PATTERN_DEFAULT);
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

}
