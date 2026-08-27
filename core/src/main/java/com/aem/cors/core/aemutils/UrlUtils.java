package com.aem.cors.core.aemutils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for URL
 */
public class UrlUtils {

    private static final String EQUALS = "=";
    private static final String SHOW_HEADER = "showHeader";
    private static final String SHOW_FOOTER = "showFooter";
    private static final String SHOW_BREADCRUMB = "showBreadcrumb";

    private UrlUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * Validate url using using OWASP
     * <p>
     * https://owasp.org/www-community/OWASP_Validation_Regex_Repository
     *
     * @param url to validate
     * @return true if valid, false if not valid
     */
    public static boolean validateUrlUsingOwasp(@NotNull String url) {
        final Pattern PATTERN = Pattern.compile("^((((https?|ftps?|gopher|telnet|nntp|tel)://)|(mailto:|news:))" +
                "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$");
        Matcher matcher = PATTERN.matcher(url);
        return matcher.matches();
    }

    public static boolean showHeader(SlingHttpServletRequest request) {
        String showHeaderParameter = request.getParameter(SHOW_HEADER);
        if (showHeaderParameter != null) {
            return Boolean.parseBoolean(showHeaderParameter);
        }
        return true;
    }

    public static boolean showFooter(SlingHttpServletRequest request) {
        String showHeaderParameter = request.getParameter(SHOW_FOOTER);
        if (showHeaderParameter != null) {
            return Boolean.parseBoolean(showHeaderParameter);
        }
        return true;
    }

    public static boolean showBreadcrumb(SlingHttpServletRequest request) {
        String showBreadcrumbParameter = request.getParameter(SHOW_BREADCRUMB);
        if (showBreadcrumbParameter != null) {
            return Boolean.parseBoolean(showBreadcrumbParameter);
        }
        return true;
    }

    public static String addParamsInUrl(@NotNull String url, Map<String, String> params) throws URISyntaxException {
        if (MapUtils.isEmpty(params)) {
            return url;
        }
        URIBuilder uriBuilder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue());
        }
        return uriBuilder.toString();
    }

    public static String encodeUrl(@NotNull String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    public static String getRequestParam(SlingHttpServletRequest request, String parameter) {
        return request != null ? request.getParameter(parameter) : null;
    }

    public static void addParam(StringJoiner joiner, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            joiner.add(key + EQUALS + value);
        }
    }
}
