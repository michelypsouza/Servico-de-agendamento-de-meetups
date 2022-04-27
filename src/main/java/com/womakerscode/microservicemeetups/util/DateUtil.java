package com.womakerscode.microservicemeetups.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {

    public static final String DATE_PATTERN_DEFAULT = "dd/MM/yyyy";

    public static String formatDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
        format.setLenient(false);
        return format.format(date);
    }

    public static Date getCurrentDate() {
        return GregorianCalendar.getInstance(new Locale("pt", "BR")).getTime();
    }

    public static Date getDateWithZeroTime(int year, int month, int day) {
        Calendar calendar = GregorianCalendar.getInstance(new Locale("pt", "BR"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return getZeroTime(calendar);
    }

    private static Date getZeroTime(Calendar calendar) {
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
