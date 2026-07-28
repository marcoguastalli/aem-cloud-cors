package com.aem.cors.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static com.aem.cors.core.CoreConstants.HTTP_HEADER_UUID;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class HttpUtils {

    private HttpUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static String getTrackingId(@NotNull HttpServletRequest request) {
        final String idFromHeader = request.getHeader(HTTP_HEADER_UUID);
        if (isNotBlank(idFromHeader)) {
            return idFromHeader;
        }
        return getIdFromSession(request);
    }

    private static String getIdFromSession(@NotNull HttpServletRequest request) {
        if (null != request.getSession() && null != request.getSession().getId()) {
            return request.getSession().getId();
        }
        return EMPTY;
    }

    public static List<String> verifyMandatoryRequestParameter(@NotNull SlingHttpServletRequest request,
                                                               @NotNull String... paramName) {
        List<String> result = new ArrayList<>();
        for (String param : paramName) {
            String paramValue = request.getParameter(param);
            if (isBlank(paramValue)) {
                result.add(format("The '%s' parameter is mandatory. ", param));
            }
        }
        return Collections.unmodifiableList(result);
    }

    public static String getHttpHeadersAsString(@NotNull HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (null == headerNames) {
            return "No headers found in the request.";
        }
        StringBuilder result = new StringBuilder("HTTP Headers: ");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            result.append(headerName)
                    .append(": ")
                    .append(request.getHeader(headerName))
                    .append(" ");
        }
        return result.toString();
    }

    public static String getHttpHeaderValue(@NotNull HttpServletRequest request, @NotNull String headerName, @NotNull String defaultValue) {
        return defaultIfEmpty(request.getHeader(headerName), defaultValue);
    }
}
