package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;

import static com.aem.cors.core.utils.CookieUtils.createCookie;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class CookieUtilsTest {

    @Test
    void testCreateCookie() {
        Cookie cookie = createCookie("session", "abc123", 3600, "/app");

        assertThat(cookie, notNullValue());
        assertThat(cookie.getName(), is("session"));
        assertThat(cookie.getValue(), is("abc123"));
        assertThat(cookie.getMaxAge(), is(3600));
        assertThat(cookie.getPath(), is("/app"));
    }
}
