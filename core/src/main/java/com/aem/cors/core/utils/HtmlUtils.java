package com.aem.cors.core.utils;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class HtmlUtils {

    private static final String APOSTROPHE = "'";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String CRLF = "\r\n";
    private static final String HTML_BREAK_LINE = "<br/>";
    private static final String HYPHEN = "‐";
    private static final String HYPHEN_MINUS = "-";
    private static final String INVALID_APOSTROPHES_REGEX = "[`´′’‘]";
    private static final String LINE_FEED = "\n";

    private HtmlUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static String replaceHyphenMinus(String str) {
        return str.replaceAll(HYPHEN_MINUS, HYPHEN);
    }

    public static String replaceInvalidApostrophes(String str) {
        return str.replaceAll(INVALID_APOSTROPHES_REGEX, APOSTROPHE);
    }

    /** Method used to replace all types of line break with the HTML <br/>
     * tag. This method is implemented for use with TextArea, where the user can create line breaks that will not show up when the value is
     * rendered.
     *
     * @param str The value in which we will replace line breaks.
     * @return If the input string is null, we will return an empty Optional, otherwise we will return an Optional containing the String
     *         with the replaced line breaks. */
    public static Optional<String> replaceLineBreakWithHtml(String str) {
        return Optional.ofNullable(str)
                .filter(StringUtils::isNotBlank)
                .map(s -> str.replace(CRLF, HTML_BREAK_LINE))
                .map(s -> s.replace(LINE_FEED, HTML_BREAK_LINE))
                .map(s -> s.replace(CARRIAGE_RETURN, HTML_BREAK_LINE));
    }

}
