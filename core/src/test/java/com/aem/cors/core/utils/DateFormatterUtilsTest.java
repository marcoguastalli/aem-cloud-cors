package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.aem.cors.core.utils.DateFormatterUtils.getFormattedTravelValidDate;
import static com.aem.cors.core.utils.DateFormatterUtils.getNowWithFormat;
import static com.aem.cors.core.utils.DateFormatterUtils.parseDateFormatDDMMYYY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class DateFormatterUtilsTest {

    @Test
    void testParseDateFormatDDMMYYYNull() {
        assertThat(parseDateFormatDDMMYYY(null), nullValue());
    }

    @Test
    void testParseDateFormatDDMMYYY() {
        Date date = new GregorianCalendar(2024, Calendar.DECEMBER, 25).getTime();
        assertThat(parseDateFormatDDMMYYY(date), is("25/12/2024"));
    }

    @Test
    void testGetNowWithFormat() {
        String result = getNowWithFormat("yyyy");
        assertThat(result, notNullValue());
        assertThat(result.length(), is(4));
    }

    @Test
    void testGetFormattedTravelValidDate() {
        Date date = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();
        assertThat(getFormattedTravelValidDate(date), is("01/01/2024"));
    }
}
