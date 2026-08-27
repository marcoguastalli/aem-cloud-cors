package com.aem.cors.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtilsNeo {

    private UrlUtilsNeo() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Validate url
     *
     * @param url to validate
     * @return true if valid, false if not valid */
    public static boolean validateUrlForPoll(final String url) {
        final Pattern PATTERN = Pattern
                .compile("(http://localhost:(7000|7001)\\/.*|https://[-a-zA-Z0-9&#/%_!:,.()]*[-a-zA-Z0-9+&@#/%=~_|])");
        Matcher matcher = PATTERN.matcher(url);
        return matcher.matches();
    }

    /** Validate url using using OWASP
     *
     * https://owasp.org/www-community/OWASP_Validation_Regex_Repository
     *
     * @param url to validate
     * @return true if valid, false if not valid */
    public static boolean validateUrlUsingOwasp(final String url) {
        final Pattern PATTERN = Pattern.compile("^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
                "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$");
        Matcher matcher = PATTERN.matcher(url);
        return matcher.matches();
    }

}
