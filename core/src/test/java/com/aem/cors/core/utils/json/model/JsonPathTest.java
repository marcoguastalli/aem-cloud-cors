package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonPathTest {

    @Test
    void testDefaultConstructorHasNullPath() {
        JsonPath jsonPath = new JsonPath();
        assertThat(jsonPath.getPath(), nullValue());
    }

    @Test
    void testEqualsAndHashCode() {
        JsonPath a = new JsonPath();
        JsonPath b = new JsonPath();
        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a.equals("not a JsonPath"), is(false));
    }

    @Test
    void testToString() {
        assertThat(new JsonPath().toString(), is("{path: 'null'}"));
    }
}
