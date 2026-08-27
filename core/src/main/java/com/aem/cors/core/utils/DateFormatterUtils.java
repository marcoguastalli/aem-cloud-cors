package com.aem.cors.core.utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Util class for date formatting
 */
public class DateFormatterUtils {

    private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    private static final TimeZone TIMEZONE_MADRID = TimeZone.getTimeZone("Europe/Madrid");

    private DateFormatterUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static String parseDateFormatDDMMYYY(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
            dateFormat.setTimeZone(TIMEZONE_MADRID);
            return dateFormat.format(date);
        }
        return null;
    }

    public static String getNowWithFormat(@NotNull String format) {
        return java.time.format.DateTimeFormatter.ofPattern(format).format(java.time.Instant.ofEpochMilli(System.currentTimeMillis()).atZone(java.time.ZoneId.systemDefault()));
    }

    public static String getFormattedTravelValidDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

}
