package com.womakerscode.microservicemeetups.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//@Component
public class DateUtil {

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd";

    public static String formatLocalDateToString(LocalDate localDateTime) {
        return DateTimeFormatter.ofPattern(DATE_PATTERN_DEFAULT).format(localDateTime);
    }

    public static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern(DATE_PATTERN_DEFAULT));
    }
    
}
