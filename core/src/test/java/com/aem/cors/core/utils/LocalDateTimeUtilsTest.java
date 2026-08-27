package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.aem.cors.core.utils.LocalDateTimeUtils.createLocalDateFromString;
import static com.aem.cors.core.utils.LocalDateTimeUtils.getDifferenceBetweenZurichAndUtc;
import static com.aem.cors.core.utils.LocalDateTimeUtils.getLocalDateTimeInUtc;
import static com.aem.cors.core.utils.LocalDateTimeUtils.getLocalDateTimeInUtcFormatted;
import static com.aem.cors.core.utils.LocalDateTimeUtils.getLocalDateTimeInZurich;
import static com.aem.cors.core.utils.LocalDateTimeUtils.getLocalDateTimeInZurichFormatted;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class LocalDateTimeUtilsTest {

    @Test
    void testCreateLocalDateFromString() {
        LocalDateTime result = createLocalDateFromString("2024-06-01T12:00:00");
        assertThat(result.getYear(), is(2024));
        assertThat(result.getMonthValue(), is(6));
        assertThat(result.getDayOfMonth(), is(1));
    }

    @Test
    void testGetLocalDateTimeInZurichNotNull() {
        assertThat(getLocalDateTimeInZurich(), notNullValue());
    }

    @Test
    void testGetLocalDateTimeInUtcNotNull() {
        assertThat(getLocalDateTimeInUtc(), notNullValue());
    }

    @Test
    void testGetLocalDateTimeInZurichFormatted() {
        String result = getLocalDateTimeInZurichFormatted("yyyy");
        assertThat(result.length(), is(4));
    }

    @Test
    void testGetLocalDateTimeInUtcFormatted() {
        String result = getLocalDateTimeInUtcFormatted("yyyy");
        assertThat(result.length(), is(4));
    }

    @Test
    void testGetDifferenceBetweenZurichAndUtcIsSmall() {
        int diff = getDifferenceBetweenZurichAndUtc();
        assertThat(diff >= -1 && diff <= 2, is(true));
    }
}
