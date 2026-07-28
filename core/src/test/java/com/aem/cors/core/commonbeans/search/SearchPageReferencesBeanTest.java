package com.aem.cors.core.commonbeans.search;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class SearchPageReferencesBeanTest {

    @Test
    void tesBean() {
        final Map<String, SearchResultPage> references = new HashMap<>();
        final SearchPageReferencesBean result = new SearchPageReferencesBean(references);
        assertThat(result, notNullValue());
        assertThat(result.getReferences(), notNullValue());
    }
}
