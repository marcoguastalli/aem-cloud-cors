package com.aem.cors.core.aemutils;

import static com.aem.cors.core.utils.LoggerUtilsNeo.logDebugTrId;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logWarnTrId;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class to make working with the HTTPComponents HTTPClient easier. */
public final class HttpUtilsNeo {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtilsNeo.class);

    private static final String COMMA_SEPARATOR = ",";
    private static final String HTTP_HEADER_UNIQUE_ID = "X-Request-Id";

    public static final List<String> FILTERED_HEADERS = new ArrayList<>();
    static {
        FILTERED_HEADERS.add(HttpHeaders.ACCEPT_CHARSET);
        FILTERED_HEADERS.add(HttpHeaders.CONTENT_LENGTH);
        FILTERED_HEADERS.add(HttpHeaders.CONTENT_TYPE);
    }

    private HttpUtilsNeo() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Converts the given {@code cookie} to a HttpClient compatible cookie.
     *
     * With RFC 6265 compliant cookie specification the cookie-domain is mandatory
     *
     * @param cookie The javax.servlet.http.Cookie that has to be converted to a HttpClient compatible cookie.
     *
     * @return either the converted cookie or {@code null} if the given {@code cookie} is 'null' or can not be converted. */
    public static ClientCookie convertCookie(final Cookie cookie) {
        if (cookie == null || StringUtils.isEmpty(cookie.getDomain())) {
            return null;
        }

        final String originalCookieName = cookie.getName();
        final String originalCookieValue = cookie.getValue();
        final String originalCookieComment = cookie.getComment();
        final String originalCookieDomain = cookie.getDomain();
        final String originalCookiePath = cookie.getPath();
        final boolean originalCookieSecure = cookie.getSecure();
        final int originalCookieVersion = cookie.getVersion();
        final int originalCookieMaxAge = cookie.getMaxAge();

        final BasicClientCookie convertedCookie = new BasicClientCookie(originalCookieName, originalCookieValue);
        convertedCookie.setComment(originalCookieComment);
        convertedCookie.setDomain(originalCookieDomain);
        convertedCookie.setPath(originalCookiePath);
        convertedCookie.setSecure(originalCookieSecure);
        convertedCookie.setVersion(originalCookieVersion);
        convertedCookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");

        /*
         * Sadly, the servlet cookie (original cookie) uses a maximum age (in seconds) while the HttpClient cookie that is the conversion
         * target uses a fixed date (expiry date). Therefore, the maximum age has to be converted to a fixed date.
         */
        if (originalCookieMaxAge > 0) {
            final Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, originalCookieMaxAge);
            convertedCookie.setExpiryDate(now.getTime());
        }

        return convertedCookie;
    }

    /** Copies all headers from the given {@code currentRequest} to the {@code outgoingRequest}.
     *
     * Headers in the input filteredHeaders will not be copied
     *
     * 'Content-Type' and 'Content-Length' header are not related with GET requests
     *
     * @param currentRequest the current servlet request from which the headers have to be copied to the outgoing request
     * @param outgoingRequest the request that is going to be send
     * @param filteredHeaders Headers that will not be copied */
    public static void copyHeaders(final SlingHttpServletRequest currentRequest,
            final HttpRequestBase outgoingRequest, final List<String> filteredHeaders) {
        LOG.debug("Started copying headers. trID: '{}'.", getTransactionId(currentRequest));
        if (currentRequest == null) {
            LOG.warn("The given 'currentRequest' is 'null', Headers are not copied.");
            return;
        }
        final Enumeration<String> headerNames = currentRequest.getHeaderNames();
        if (headerNames == null) {
            LOG.warn("The 'headerNames' from given 'currentRequest' is 'null', Headers are not copied.");
            return;
        }

        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            if (filteredHeaders.contains(headerName)) {
                continue;
            }
            final String headerValue = currentRequest.getHeader(headerName);
            outgoingRequest.addHeader(headerName, headerValue);
            LOG.debug("Copied header '{}' with value '{}'. trID: '{}'.", headerName, headerValue, getTransactionId(currentRequest));
        }

    }

    /** Creates a HttpClient cookie store from the given cookies.
     *
     * @param cookies Cookies to include in the cookie store.
     * @param transactionId the transaction id of the current request.
     * @return either cookie store that contains all non-null cookies from the given array of {@code cookies} or an empty cookie store if
     *         the given array of {@code cookies} is null or all of its values are null. */
    public static CookieStore createCookieStore(final Cookie[] cookies, final String transactionId) {
        LOG.debug("Started creating cookie store. trID: '{}'.", transactionId);
        final BasicCookieStore cookieStore = new BasicCookieStore();

        if (cookies == null) {
            LOG.warn("Cookies array was 'null'. Returning empty cookie store. trID: '{}'.", transactionId);
            return cookieStore;
        }

        for (final Cookie cookie : cookies) {
            final ClientCookie convertedCookie = convertCookie(cookie);
            if (convertedCookie != null) {
                cookieStore.addCookie(convertedCookie);
                LOG.debug("Added cookie with name '{}', domain '{}' and value '{}' to cookie store. trID: '{}'.",
                        convertedCookie.getName(),
                        convertedCookie.getDomain(),
                        convertedCookie.getValue(),
                        transactionId);
            }
        }

        LOG.debug("Finished creating cookie store. Number of cookies: '{}'. trID: '{}'.", cookieStore.getCookies().size(), transactionId);
        return cookieStore;
    }

    /** Returns the HEADER_UNIQUE_ID from the input request if present
     *
     * As in localhost and non-header environments we dont have it, it returns the session-id
     *
     * @param request the request that contains header
     *
     * @return either the header value, the session-id, or empty */
    public static String getTransactionId(final HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }
        final String idFromHeader = request.getHeader(HTTP_HEADER_UNIQUE_ID);
        if (StringUtils.isNotBlank(idFromHeader)) {
            return idFromHeader;
        }
        return getIdFromSession(request);
    }

    /** Get the session id from the input request
     *
     * @param request a Request
     * @return a String */
    private static String getIdFromSession(HttpServletRequest request) {
        if (request != null && request.getSession() != null && request.getSession().getId() != null) {
            return request.getSession().getId();
        }
        return StringUtils.EMPTY;
    }

    /** Log the input request info, headers, attributes, parameters
     *
     * @param logger to log using the implementation class name
     * @param transactionId the HEADER_UNIQUE_ID
     * @param servletRequest the request */
    public static void logRequest(final Logger logger, final String transactionId, final ServletRequest servletRequest) {
        if (servletRequest == null) {
            return;
        }
        try {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
            logDebugTrId(logger, transactionId, String.format("START LOG FOR REQUEST: %s", request.getRequestPathInfo()));

            logDebugTrId(logger, transactionId,
                    String.format("Resource Path: %s", defaultString(request.getRequestPathInfo().getResourcePath())));
            logDebugTrId(logger, transactionId,
                    String.format("Selector String: %s", defaultString(request.getRequestPathInfo().getSelectorString())));
            for (int i = 0; i < request.getRequestPathInfo().getSelectors().length; i++) {
                logDebugTrId(logger, transactionId, String.format("Selector: %s", request.getRequestPathInfo().getSelectors()[i]));
            }
            logDebugTrId(logger, transactionId, String.format("Extension: %s", defaultString(request.getRequestPathInfo().getExtension())));
            logDebugTrId(logger, transactionId, String.format("Suffix: %s", defaultString(request.getRequestPathInfo().getSuffix())));
            logDebugTrId(logger, transactionId, String.format("Path Info: %s", defaultString(request.getPathInfo())));
            logDebugTrId(logger, transactionId, String.format("Method: %s", defaultString(request.getMethod())));
            logDebugTrId(logger, transactionId, String.format("Character Encoding: %s", defaultString(request.getCharacterEncoding())));
            logDebugTrId(logger, transactionId, String.format("Content Type: %s", defaultString(request.getContentType())));
            logDebugTrId(logger, transactionId, String.format("Request Session Id: %s", defaultString(request.getSession().getId())));
            // Request Headers
            for (Enumeration<String> enumeration = request.getHeaderNames(); enumeration.hasMoreElements();) {
                final String header = enumeration.nextElement();
                logDebugTrId(logger, transactionId, String.format("Request Header '%s' has value: %s", header, request.getHeader(header)));
            }
            // Request Attributes
            for (Enumeration<String> enumeration = request.getAttributeNames(); enumeration.hasMoreElements();) {
                final String attribute = enumeration.nextElement();
                logDebugTrId(logger, transactionId,
                        String.format("Request Attribute '%s' has value: %s", attribute, request.getAttribute(attribute)));
            }
            // Request Parameters
            final Map<String, String[]> parameters = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                logDebugTrId(logger, transactionId,
                        String.format("Request Parameter '%s' has value: %s", entry.getKey(), entry.getValue()));
            }
            logDebugTrId(logger, transactionId, "END LOG FOR REQUEST");
        } catch (Exception e) {
            logger.error("Error log Request", e);
        }
    }

    /** log the value of the input headerName for the input slingHttpServletRequest
     *
     * @param logger to log using the implementation class name
     * @param transactionId the HEADER_UNIQUE_ID
     * @param slingHttpServletRequest the request
     * @param headerName the name of the Header to log */
    public static void logRequestHeader(final Logger logger, final String transactionId,
            final SlingHttpServletRequest slingHttpServletRequest, final String headerName) {
        if (slingHttpServletRequest == null) {
            return;
        }
        logWarnTrId(logger, transactionId,
                String.format("Request Header '%s' has value: %s", headerName, slingHttpServletRequest.getHeader(headerName)));
    }

    /** Log the input response headers
     *
     * @param logger to log using the implementation class name
     * @param servletResponse the response */
    public static void logResponse(final Logger logger, final ServletResponse servletResponse) {
        if (servletResponse == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            try {
                final SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;
                // Response headers
                for (final String header : response.getHeaderNames()) {
                    logger.debug(" - Response Header '{}' has value: {}", header, response.getHeader(header));
                }
            } catch (Exception e) {
                logger.error("Error log Response", e);
            }
        }
    }

    /** Convert the input parameters to a Map
     *
     * @param parameters to convert
     * @return a Map */
    public static Map<String, String> convertParametersArray(final Map<String, String[]> parameters) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            final String[] paramValue = entry.getValue();
            if (paramValue.length == 1) {
                result.put(entry.getKey(), paramValue[0]);
            } else {
                result.put(entry.getKey(), String.join(COMMA_SEPARATOR, paramValue));
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /** Convert the input parameters to BasicNameValuePair
     *
     * @param parameters to convert
     * @return a List of BasicNameValuePair */
    public static List<BasicNameValuePair> convertParameters(final Map<String, String> parameters) {
        List<BasicNameValuePair> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return Collections.unmodifiableList(result);
    }

    /** Copy the Headers from the httpRequest to the slingHttpServletRequest
     *
     * The header is copied only if not in the input blacklist
     *
     * @param slingHttpServletRequest the Sling Request
     * @param httpRequest an httpRequest
     * @param blacklist a Set of headers to ignore */
    public static void copyRequestHeaders(final SlingHttpServletRequest slingHttpServletRequest, final HttpRequest httpRequest,
            final Set<String> blacklist) {
        if (slingHttpServletRequest != null) {
            Enumeration<String> headerName = slingHttpServletRequest.getHeaderNames();
            while (headerName.hasMoreElements()) {
                final String name = headerName.nextElement();
                if (!blacklist.contains(name)) {
                    httpRequest.addHeader(new BasicHeader(name, slingHttpServletRequest.getHeader(name)));
                }
            }
        }
    }

    /** Copy the Headers from the httpResponse to the slingHttpServletResponse
     *
     * The header is copied only if not in the input blacklist
     *
     * @param slingHttpServletResponse the Sling Response
     * @param httpResponse an httpResponse
     * @param blacklist a Set of headers to ignore */
    public static void copyResponseHeaders(final SlingHttpServletResponse slingHttpServletResponse, final HttpResponse httpResponse,
            final Set<String> blacklist) {
        final Header[] responseHeaders = httpResponse.getAllHeaders();
        if (responseHeaders != null) {
            for (final Header header : responseHeaders) {
                final String name = header.getName();
                if (!blacklist.contains(name)) {
                    slingHttpServletResponse.addHeader(name, header.getValue());
                }
            }
        }
    }

}
