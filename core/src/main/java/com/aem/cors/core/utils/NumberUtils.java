package com.aem.cors.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/** Util class for Number operations */
public class NumberUtils {

    private NumberUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    private static final Pattern PATTERN_NUMBERS_ONLY = Pattern.compile("\\d+");

    /** Given a String that contains String, returns only the numbers
     * 
     * This is used to parse the width and height of the SVG/XML attributes that can contains chars like '560pt'
     * 
     * @param s a String
     * @return a Long */
    public static Long extractLongFromString(@NotNull final String s) {
        final Matcher matcher = PATTERN_NUMBERS_ONLY.matcher(s);
        if (matcher.find()) {
            return Long.valueOf(matcher.group());
        }
        return 0L;
    }

}
