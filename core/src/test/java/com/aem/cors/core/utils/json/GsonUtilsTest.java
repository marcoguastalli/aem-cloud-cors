package com.aem.cors.core.utils.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import com.aem.cors.core.utils.rest.domain.AbstractRestResponse;

import static com.aem.cors.core.utils.json.GsonUtils.createGsonStringFromList;
import static com.aem.cors.core.utils.json.GsonUtils.createGsonStringFromObject;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class GsonUtilsTest {

    private static class TestRestResponse extends AbstractRestResponse {
        private final String name;

        TestRestResponse(String name) {
            this.name = name;
        }
    }

    @Test
    void testCreateGsonStringFromObject() {
        String json = createGsonStringFromObject(new TestRestResponse("foo"));
        assertThat(json, containsString("\"name\":\"foo\""));
    }

    @Test
    void testCreateGsonStringFromObjectSerializeNullsFalseOmitsNull() {
        String json = createGsonStringFromObject(new TestRestResponse(null), false);
        assertThat(json, is("{}"));
    }

    @Test
    void testCreateGsonStringFromObjectSerializeNullsTrueIncludesNull() {
        String json = createGsonStringFromObject(new TestRestResponse(null), true);
        assertThat(json, containsString("\"name\":null"));
    }

    @Test
    void testCreateGsonStringFromList() {
        String json = createGsonStringFromList(List.of(new TestRestResponse("a"), new TestRestResponse("b")));
        assertThat(json, containsString("\"name\":\"a\""));
        assertThat(json, containsString("\"name\":\"b\""));
    }
}
