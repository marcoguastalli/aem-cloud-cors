package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseResultTest {

    @Test
    void testDefaultConstructor() {
        assertThat(new JsonResponseResult().getResult(), nullValue());
    }

    @Test
    void testGetterAndSetter() {
        JsonResponseResult result = new JsonResponseResult("value");
        assertThat(result.getResult(), is("value"));

        result.setResult("other");
        assertThat(result.getResult(), is("other"));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseResult a = new JsonResponseResult("value");
        JsonResponseResult b = new JsonResponseResult("value");
        JsonResponseResult c = new JsonResponseResult("different");

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }
}
