package com.aem.cors.core.commonbeans.search;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME;
import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTIES;
import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTY_TEXT;
import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_TITLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class SearchResultPageTest {

    @Test
    void tesBean() {
        final SearchResultPage searchResultPage = SearchResultPage.builder()
                .pagePath(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME)
                .pageTitle(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_TITLE)
                .properties(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTIES)
                .build();
        final Map<String, SearchResultPage> result = Map.of(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME, searchResultPage);
        assertThat(result, notNullValue());
        final SearchResultPage searchResultPageResult = result.get(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME);
        assertThat(searchResultPageResult, notNullValue());
        assertThat(searchResultPageResult.getPagePath(), is(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME));
        assertThat(searchResultPageResult.getPageTitle(), is(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_TITLE));
        assertThat(searchResultPageResult.getProperties(), is(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTIES));
        assertThat(searchResultPageResult.getProperties().contains(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME_PROPERTY_TEXT), is(Boolean.TRUE));
    }
}
