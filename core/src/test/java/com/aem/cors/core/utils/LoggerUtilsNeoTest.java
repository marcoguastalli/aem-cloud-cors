package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.aem.cors.core.utils.LoggerUtilsNeo.logDebugTrId;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logErrorTrId;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logHttpRequestParameters;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logInfoTrId;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logWarnTrId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggerUtilsNeoTest {

    @Mock
    Logger logger;

    @Test
    void testLogDebugTrIdEnabled() {
        when(logger.isDebugEnabled()).thenReturn(true);
        logDebugTrId(logger, "tr-1", "message");
        verify(logger, times(1)).debug(anyString(), anyString(), anyString());
    }

    @Test
    void testLogDebugTrIdDisabled() {
        when(logger.isDebugEnabled()).thenReturn(false);
        logDebugTrId(logger, "tr-1", "message");
        verify(logger, never()).debug(anyString(), anyString(), anyString());
    }

    @Test
    void testLogInfoTrIdEnabled() {
        when(logger.isInfoEnabled()).thenReturn(true);
        logInfoTrId(logger, "tr-1", "message");
        verify(logger, times(1)).info(anyString(), anyString(), anyString());
    }

    @Test
    void testLogWarnTrId() {
        logWarnTrId(logger, "tr-1", "message");
        verify(logger, times(1)).warn(anyString(), anyString(), anyString());
    }

    @Test
    void testLogErrorTrIdWithExceptionEnabled() {
        when(logger.isErrorEnabled()).thenReturn(true);
        Exception e = new RuntimeException("boom");
        logErrorTrId(logger, "tr-1", "message", e);
        verify(logger, times(1)).error(anyString(), any(Exception.class));
    }

    @Test
    void testLogErrorTrIdWithoutExceptionEnabled() {
        when(logger.isErrorEnabled()).thenReturn(true);
        logErrorTrId(logger, "tr-1", "message");
        verify(logger, times(1)).error(anyString());
    }

    @Test
    void testLogHttpRequestParameters() {
        when(logger.isInfoEnabled()).thenReturn(true);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("a", "1");
        params.put("b", "2");

        logHttpRequestParameters(logger, "tr-1", params);

        verify(logger, times(2)).info(anyString(), anyString(), anyString());
    }
}
