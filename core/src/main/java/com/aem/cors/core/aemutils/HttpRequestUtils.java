package com.aem.cors.core.aemutils;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;

/** Http Request Utils Class */
public class HttpRequestUtils {

    private HttpRequestUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param slingHttpServletRequest a request
     * @param parameterName the parameter name to be retrieved
     * @return the corresponding value as String or and empty Optional */
    public static Optional<String> getRequestParameterAsString(final SlingHttpServletRequest slingHttpServletRequest,
            final String parameterName) {
        return Optional.ofNullable(slingHttpServletRequest)
                .map(r -> r.getRequestParameter(parameterName))
                .map(RequestParameter::getString)
                .filter(StringUtils::isNotBlank);
    }

    /** @param slingHttpServletRequest a request
     * @param parameterName the parameter name to be retrieved
     * @return the corresponding value as Integer or and empty Optional */
    public static Optional<Integer> getRequestParameterAsInteger(final SlingHttpServletRequest slingHttpServletRequest,
            final String parameterName) {
        return getRequestParameterAsString(slingHttpServletRequest, parameterName)
                .filter(NumberUtils::isDigits)
                .map(NumberUtils::createInteger);
    }

    /** @param slingHttpServletRequest a request
     * @param headerName the header name to be retrieved
     * @return the corresponding value as String or and empty Optional */
    public static Optional<String> getRequestHeaderAsString(final SlingHttpServletRequest slingHttpServletRequest,
            final String headerName) {
        return Optional.ofNullable(slingHttpServletRequest)
                .map(r -> slingHttpServletRequest.getHeader(headerName))
                .filter(StringUtils::isNotBlank);
    }

    /** @param slingHttpServletRequest a request
     * @param headerName the header name to be retrieved
     * @return the corresponding value as Boolean */
    public static Optional<Boolean> getRequestHeaderAsBoolean(final SlingHttpServletRequest slingHttpServletRequest,
            final String headerName) {
        return getRequestHeaderAsString(slingHttpServletRequest, headerName)
                .map(BooleanUtils::toBoolean);
    }

    /** @param slingHttpServletRequest a request
     * @param attributeName the attribute name to be retrieved
     * @return the corresponding value as T or and empty Optional */
    public static <T> Optional<T> getRequestAttributeAs(final SlingHttpServletRequest slingHttpServletRequest,
            final String attributeName, Class<T> attributeType) {
        return Optional.ofNullable(slingHttpServletRequest)
                .map(r -> r.getAttribute(attributeName))
                .filter(object -> attributeType.isAssignableFrom(object.getClass()))
                .map(attributeType::cast);
    }

}
