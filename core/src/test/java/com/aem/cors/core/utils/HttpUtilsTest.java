package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.aem.cors.core.CoreConstants.HTTP_HEADER_UUID;
import static com.aem.cors.core.utils.HttpUtils.getTrackingId;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpUtilsTest {

    static final String HTTP_HEADER_UUID_VALUE = "tracking_id_123";
    static final String HTTP_SESSION_ID = "session_id_123";

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    @Test
    void testGetTrackingIdEmpty() {
        assertThat(getTrackingId(request), notNullValue());
        assertThat(getTrackingId(request), is(EMPTY));
    }

    @Test
    void testGetTrackingIdWithHeaderValue() {
        when(request.getHeader(HTTP_HEADER_UUID)).thenReturn(HTTP_HEADER_UUID_VALUE);
        assertThat(getTrackingId(request), notNullValue());
        assertThat(getTrackingId(request), is(HTTP_HEADER_UUID_VALUE));
    }

    @Test
    void testGetTrackingIdWithSession() {
        when(session.getId()).thenReturn(HTTP_SESSION_ID);
        when(request.getSession()).thenReturn(session);
        assertThat(getTrackingId(request), notNullValue());
        assertThat(getTrackingId(request), is(HTTP_SESSION_ID));
    }

    @Test
    void testGetHttpHeadersAsStringWithHeaders() {
        when(request.getHeaderNames()).thenReturn(new java.util.Vector<>(java.util.List.of("Content-Type", "Accept")).elements());
        when(request.getHeader("Content-Type")).thenReturn("application/json");
        when(request.getHeader("Accept")).thenReturn("application/json");

        String result = HttpUtils.getHttpHeadersAsString(request);
        assertThat(result, notNullValue());
        assertThat(result, containsString("HTTP Headers:"));
        assertThat(result, containsString("Content-Type"));
    }

    @Test
    void testGetHttpHeadersAsStringNoHeaders() {
        when(request.getHeaderNames()).thenReturn(null);

        String result = HttpUtils.getHttpHeadersAsString(request);
        assertThat(result, is("No headers found in the request."));
    }

    @Test
    void testGetHttpHeaderValue() {
        when(request.getHeader("X-Custom-Header")).thenReturn("custom-value");

        String result = HttpUtils.getHttpHeaderValue(request, "X-Custom-Header", "default");
        assertThat(result, is("custom-value"));
    }

    @Test
    void testGetHttpHeaderValueWithDefault() {
        when(request.getHeader("X-Missing-Header")).thenReturn(null);

        String result = HttpUtils.getHttpHeaderValue(request, "X-Missing-Header", "default-value");
        assertThat(result, is("default-value"));
    }

}
