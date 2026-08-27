package com.aem.cors.core.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Util class for Strings
 */
public class StringsUtils {

    private StringsUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * The idea is to always return a String, and never return a null
     *
     * @param s can be null
     * @return a Base64 s or EMPTY
     */
    public static String returnEncodedString(@Nullable String s) {
        if (isNotEmpty(s)) {
            return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
        }
        return EMPTY;
    }
}
