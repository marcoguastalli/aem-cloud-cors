package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.aem.cors.core.utils.CalendarUtils.adaptToLocalDateTime;
import static com.aem.cors.core.utils.CalendarUtils.format;
import static com.aem.cors.core.utils.CalendarUtils.isCalendarInThePast;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class CalendarUtilsTest {

    @Test
    void testAdaptToLocalDateTimeNull() {
        assertThat(adaptToLocalDateTime(null), nullValue());
    }

    @Test
    void testAdaptToLocalDateTime() {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(2024, Calendar.JANUARY, 15, 10, 30, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LocalDateTime result = adaptToLocalDateTime(calendar);

        assertThat(result.getYear(), is(2024));
        assertThat(result.getMonthValue(), is(1));
        assertThat(result.getDayOfMonth(), is(15));
        assertThat(result.getHour(), is(10));
        assertThat(result.getMinute(), is(30));
    }

    @Test
    void testIsCalendarInThePastNull() {
        assertThat(isCalendarInThePast(null), is(false));
    }

    @Test
    void testIsCalendarInThePastTrue() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, -1);
        assertThat(isCalendarInThePast(calendar), is(true));
    }

    @Test
    void testIsCalendarInThePastFalse() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 1);
        assertThat(isCalendarInThePast(calendar), is(false));
    }

    @Test
    void testFormatWithDefaultPattern() {
        Calendar calendar = new GregorianCalendar(2024, Calendar.MARCH, 5, 8, 0, 0);
        String result = format(calendar);
        assertThat(result, is("05-03-2024 08:00:00"));
    }

    @Test
    void testFormatWithCustomPattern() {
        Calendar calendar = new GregorianCalendar(2024, Calendar.MARCH, 5, 8, 0, 0);
        String result = format(calendar, "yyyy/MM/dd");
        assertThat(result, is("2024/03/05"));
    }
}
