package com.aem.cors.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/** Util Class for LocalDateTime */
public class LocalDateTimeUtils {

    public static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String EUROPE_ZURICH = "Europe/Zurich";
    public static final ZoneId ZONE_ZURICH = ZoneId.of(EUROPE_ZURICH);
    public static final TimeZone TIMEZONE_ZURICH = TimeZone.getTimeZone(EUROPE_ZURICH);
    public static final int TIMEZONE_ZERO = 0;

    private LocalDateTimeUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Given a String that represent a Date, parse and create a LocalDateTime
     *
     * @param strDate a String representing a Date
     * @return a LocalDateTime object */
    public static LocalDateTime createLocalDateFromString(final String strDate) {
        return LocalDateTime.parse(strDate);
    }

    /** Create a LocalDateTime object for the current date and time in ZONE_ZURICH
     *
     * @return a LocalDateTime object */
    public static LocalDateTime getLocalDateTimeInZurich() {
        final ZonedDateTime swissZonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZONE_ZURICH);
        final ZonedDateTime zonedDateTime = swissZonedDateTime.withZoneSameInstant(ZONE_ZURICH);
        return zonedDateTime.toLocalDateTime();
    }

    /** Create a LocalDateTime object for the current date and time in ZONE_ZURICH
     *
     * Then apply the input String pattern for the DateTimeFormatter
     *
     * @param pattern a String date pattern
     * @return a String object */
    public static String getLocalDateTimeInZurichFormatted(final String pattern) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return getLocalDateTimeInZurich().format(dateTimeFormatter);
    }

    /** Create a LocalDateTime object for the current date and time in UTC zone
     *
     * @return a LocalDateTime object */
    public static LocalDateTime getLocalDateTimeInUtc() {
        final ZonedDateTime utcZonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
        final ZonedDateTime zonedDateTime = utcZonedDateTime.withZoneSameInstant(ZONE_ZURICH);
        return zonedDateTime.toLocalDateTime();
    }

    /** Create a LocalDateTime object for the current date and time in UTC zone
     *
     * Then apply the input String pattern for the DateTimeFormatter
     *
     * @param pattern a String date pattern
     * @return a String object */

    public static String getLocalDateTimeInUtcFormatted(final String pattern) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return getLocalDateTimeInUtc().format(dateTimeFormatter);
    }

    /** The following algorithm:
     *
     * - creates a LocalDateTime in Zurich zone
     *
     * - creates a LocalDateTime in UTC zone
     *
     * - The difference between the two hours is returned
     *
     * @return an int value with the number of hour(s) */
    public static int getDifferenceBetweenZurichAndUtc() {
        final LocalDateTime localDateTimeInZurich = getLocalDateTimeInZurich();
        final LocalDateTime localDateTimeInUtc = getLocalDateTimeInUtc();

        int hourInZurich = localDateTimeInZurich.getHour();
        int hourInUtc = localDateTimeInUtc.getHour();

        return hourInUtc - hourInZurich;
    }

}
