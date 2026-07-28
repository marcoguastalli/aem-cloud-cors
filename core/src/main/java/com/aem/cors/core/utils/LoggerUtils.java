package com.aem.cors.core.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static java.lang.String.format;

/**
 * Util class for log operations
 */
public class LoggerUtils {

    private LoggerUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * @param log        log to log using the instance class name and not this class
     * @param trackingId the HEADER_UNIQUE_ID
     * @param message    to write in the log
     */
    public static void logDebugTrackingId(@NotNull Logger log, @NotNull String trackingId, @NotNull String message) {
        log.debug("trID: {} - {}", trackingId, message);
    }

    public static void logDebug(@NotNull Logger log, @NotNull String message) {
        log.debug("AEM CORS Debug: {}", message);
    }

    public static void logInfoTrackingId(@NotNull Logger log, @NotNull String trackingId, @NotNull String message) {
        log.info("trID: {} - {}", trackingId, message);
    }

    /**
     * @param log        log to log using the instance class name and not this class
     * @param trackingId the HEADER_UNIQUE_ID
     * @param message    to write in the log
     */
    public static void logWarnTrackingId(@NotNull Logger log, @NotNull String trackingId, @NotNull String message) {
        log.warn("trID: {} - {}", trackingId, message);
    }

    /**
     * @param log        log to log using the instance class name and not this class
     * @param trackingId the HEADER_UNIQUE_ID
     * @param message    to write in the log
     * @param e          the Exception
     */
    public static void logErrorTrackingId(@NotNull Logger log, @NotNull String trackingId, @NotNull String message, @NotNull Exception e) {
        log.error(format("trID: %s - %s", trackingId, message), e);
    }

    /**
     * Log just the error message, without the Exception
     *
     * @param log        log to log using the instance class name and not this class
     * @param trackingId the HEADER_UNIQUE_ID
     * @param message    to write in the log
     */
    public static void logErrorTrackingId(@NotNull Logger log, @NotNull String trackingId, @NotNull String message) {
        log.error(format("trID: %s - %s", trackingId, message));
    }

    /**
     * @param log                     log to log using the instance class name and not this class
     * @param trackingId              the HEADER_UNIQUE_ID
     * @param slingHttpServletRequest to be logged
     */
    public static void logHttpRequestParameters(@NotNull Logger log, @NotNull String trackingId, @NotNull SlingHttpServletRequest slingHttpServletRequest) {
        for (RequestParameter param : slingHttpServletRequest.getRequestParameterList()) {
            logInfoTrackingId(log, trackingId, format("Request Parameter '%s' has value: %s", param.getName(), param.getString()));
        }
    }
}
