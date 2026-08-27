package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.UrlUtilsNeo.validateUrlForPoll;
import static com.aem.cors.core.utils.UrlUtilsNeo.validateUrlUsingOwasp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class UrlUtilsNeoTest {

    @Test
    void testValidateUrlForPollLocalhost() {
        assertThat(validateUrlForPoll("http://localhost:7000/status"), is(true));
    }

    @Test
    void testValidateUrlForPollHttps() {
        assertThat(validateUrlForPoll("https://example.com/path"), is(true));
    }

    @Test
    void testValidateUrlForPollInvalid() {
        assertThat(validateUrlForPoll("not a url"), is(false));
    }

    @Test
    void testValidateUrlUsingOwaspValid() {
        assertThat(validateUrlUsingOwasp("https://example.com/path?q=1"), is(true));
    }

    @Test
    void testValidateUrlUsingOwaspInvalid() {
        assertThat(validateUrlUsingOwasp("not a url"), is(false));
    }
}
