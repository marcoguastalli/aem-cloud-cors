package com.aem.cors.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtils {

    public static final String DEFAULT_PATTERN = "dd-MM-yyyy HH:mm:ss";

    private CalendarUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Adapt the input calendar to a LocalDateTime
     * 
     * @param calendar to be adapted
     * @return a LocalDateTime, or null if the input is null */
    public static LocalDateTime adaptToLocalDateTime(final Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        final TimeZone timeZone = calendar.getTimeZone();
        final ZoneId zoneId = timeZone == null ? ZoneId.systemDefault() : timeZone.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
    }

    /** Check if the input calendar is in the past
     * 
     * @param calendar to be checked
     * @return true if the input calendar is in the past, false instead */
    public static boolean isCalendarInThePast(final Calendar calendar) {
        if (calendar == null) {
            return false;
        }
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expirationDateTime = adaptToLocalDateTime(calendar);
        return expirationDateTime.isBefore(now);
    }

    /** Format the input Calendar using the default pattern
     * 
     * @param calendar a Calendar
     * @return a String */
    public static String format(final Calendar calendar) {
        return format(calendar, DEFAULT_PATTERN);
    }

    /** Format the input Calendar using a pattern
     *
     * @param calendar a Calendar
     * @param pattern the pattern used to format
     * @return a String */
    public static String format(final Calendar calendar, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        df.setCalendar(calendar);
        return df.format(calendar.getTime());
    }

}
