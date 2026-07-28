package com.aem.cors.core.utils;

import com.aem.cors.core.exceptions.AemRuntimeException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static com.aem.cors.core.utils.LoggerUtils.logDebugTrackingId;
import static com.aem.cors.core.utils.LoggerUtils.logErrorTrackingId;
import static com.aem.cors.core.utils.LoggerUtils.logHttpRequestParameters;
import static com.aem.cors.core.utils.LoggerUtils.logInfoTrackingId;
import static com.aem.cors.core.utils.LoggerUtils.logWarnTrackingId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class LoggerUtilsTest {

    static final String LOG_MESSAGE = "bla bla bla";
    static final String ERROR_MESSAGE = "error";
    static final String TRACKING_ID = "session_27";
    @Mock
    Logger log;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogDebugTrackingId() {
        logDebugTrackingId(log, TRACKING_ID, LOG_MESSAGE);
        assertThat(log, notNullValue());
    }

    @Test
    void testLogDebug() {
        LoggerUtils.logDebug(log, LOG_MESSAGE);
        assertThat(log, notNullValue());
    }

    @Test
    void testLogInfoTrackingId() {
        logInfoTrackingId(log, TRACKING_ID, LOG_MESSAGE);
        assertThat(log, notNullValue());
    }

    @Test
    void testLogWarnTrackingId() {
        logWarnTrackingId(log, TRACKING_ID, LOG_MESSAGE);
        assertThat(log, notNullValue());
    }

    @Test
    void testLogErrorTrackingIdWithException() {
        logErrorTrackingId(log, TRACKING_ID, LOG_MESSAGE, new AemRuntimeException(ERROR_MESSAGE, new RuntimeException()));
        assertThat(log, notNullValue());
    }

    @Test
    void testLogErrorTrackingId() {
        logErrorTrackingId(log, TRACKING_ID, LOG_MESSAGE);
        assertThat(log, notNullValue());
    }

    @Test
    void testLogHttpRequestParameters() {
        logHttpRequestParameters(log, TRACKING_ID, slingHttpServletRequest);
        assertThat(log, notNullValue());
    }
}
