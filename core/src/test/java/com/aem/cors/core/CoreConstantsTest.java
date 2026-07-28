package com.aem.cors.core;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CoreConstantsTest {

    public static final String TEST_TRACKING_ID = "TEST_TRACKING_ID";
    public static final String JUST_A_STRING = "just a string";
    public static final String JSON_A_STRING = "{\"result\":\"" + JUST_A_STRING + "\"}";

    public static final String PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME = "/content/aemcors/language-masters/en/home";
    public static final String PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_TITLE = "Home Page";
    public static final String PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTY_TEXT = PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME.concat("jcr:content/root/container/text");
    public static final Set<String> PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTIES = Set.of(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTY_TEXT);

    public static final String PATH_CONTENT_EN_HOME = "/content/aemcors/en/home";

    @Test
    void testConstants() {
        assertThat(CoreConstants.LOCALHOST, is("localhost"));
        assertThat(CoreConstants.ENVIRONMENT_PROD, is("prod"));
        assertThat(CoreConstants.ENVIRONMENT_STAGE, is("stage"));
        assertThat(CoreConstants.ENVIRONMENT_DEV, is("dev"));
        assertThat(CoreConstants.ENVIRONMENT_RDE, is("rde"));
    }
}
