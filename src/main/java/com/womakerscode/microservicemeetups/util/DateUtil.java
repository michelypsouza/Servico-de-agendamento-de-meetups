package com.womakerscode.microservicemeetups.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

//@Component
public class DateUtil {

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd";

    public static String formatLocalDateToString(LocalDate localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("The date is invalid");
        }
        return DateTimeFormatter.ofPattern(DATE_PATTERN_DEFAULT).format(localDateTime);
    }

    public static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern(DATE_PATTERN_DEFAULT));
    }

    public static String formatDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
        format.setLenient(false);
        return format.format(date);
    }
    public static Date convertStringToDate(String stringDate) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
        format.setLenient(false);
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException("The date is invalid");
        }
    }

}
