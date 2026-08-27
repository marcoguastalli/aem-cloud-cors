package com.aem.cors.core.utils;

import java.util.Map;

import org.slf4j.Logger;

/** Log with the transactionId util class
 * 
 * Refer to HttpUtilsNeo.getTransactionId() for more details */
public class LoggerUtilsNeo {

    private LoggerUtilsNeo() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param message to write in the log */
    public static void logDebugTrId(final Logger logger, final String transactionId, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("trID: {} - {}", transactionId, message);
        }
    }

    /** @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param message to write in the log */
    public static void logInfoTrId(final Logger logger, final String transactionId, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info("trID: {} - {}", transactionId, message);
        }
    }

    /** @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param message to write in the log */
    public static void logWarnTrId(final Logger logger, final String transactionId, final String message) {
        logger.warn("trID: {} - {}", transactionId, message);
    }

    /** @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param message to write in the log
     * @param e the Exception */
    public static void logErrorTrId(final Logger logger, final String transactionId, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(String.format("trID: %s - %s", transactionId, message), e);
        }
    }

    /** Log just the error message, without the Exception
     *
     * @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param message to write in the log */
    public static void logErrorTrId(final Logger logger, final String transactionId, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(String.format("trID: %s - %s", transactionId, message));
        }
    }

    /** @param logger logger to log using the instance class name and not this class
     * @param transactionId the HEADER_UNIQUE_ID
     * @param parameters to be logged */
    public static void logHttpRequestParameters(final Logger logger, final String transactionId, final Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            logInfoTrId(logger, transactionId, String.format("Request Parameter '%s' has value: %s", entry.getKey(), entry.getValue()));
        }
    }
}
