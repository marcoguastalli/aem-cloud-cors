package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.NumberUtils.extractLongFromString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class NumberUtilsTest {

    @Test
    void testExtractLongFromStringWithSuffix() {
        assertThat(extractLongFromString("560pt"), is(560L));
    }

    @Test
    void testExtractLongFromStringNoDigits() {
        assertThat(extractLongFromString("pt"), is(0L));
    }

    @Test
    void testExtractLongFromStringOnlyDigits() {
        assertThat(extractLongFromString("42"), is(42L));
    }
}
