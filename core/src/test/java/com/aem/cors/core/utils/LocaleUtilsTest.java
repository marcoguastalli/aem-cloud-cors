package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.aem.cors.core.utils.LocaleUtils.getLocaleFromLanguageIsoCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class LocaleUtilsTest {

    @Test
    void testGetLocaleFromLanguageIsoCode() {
        assertThat(getLocaleFromLanguageIsoCode("en"), is(Locale.forLanguageTag("en")));
    }

    @Test
    void testGetLocaleFromLanguageIsoCodeWithRegion() {
        Locale result = getLocaleFromLanguageIsoCode("es-ES");
        assertThat(result.getLanguage(), is("es"));
        assertThat(result.getCountry(), is("ES"));
    }
}
