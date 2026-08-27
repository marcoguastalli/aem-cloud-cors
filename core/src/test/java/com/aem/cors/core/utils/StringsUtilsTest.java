package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.StringsUtils.returnEncodedString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StringsUtilsTest {

    @Test
    void testReturnEncodedString() {
        assertThat(returnEncodedString("hello"), is("aGVsbG8="));
    }

    @Test
    void testReturnEncodedStringNull() {
        assertThat(returnEncodedString(null), is(EMPTY));
    }

    @Test
    void testReturnEncodedStringEmpty() {
        assertThat(returnEncodedString(""), is(EMPTY));
    }
}
