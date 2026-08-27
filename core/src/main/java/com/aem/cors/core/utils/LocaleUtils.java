package com.aem.cors.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Util class for Locale
 */
public class LocaleUtils {

    private LocaleUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static Locale getLocaleFromLanguageIsoCode(@NotNull String languageIsoCode) {
        return Locale.forLanguageTag(languageIsoCode);
    }
}
