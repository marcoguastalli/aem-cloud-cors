package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseStatusTest {

    @Test
    void testGetter() {
        assertThat(new JsonResponseStatus("ok").getStatus(), is("ok"));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseStatus a = new JsonResponseStatus("ok");
        JsonResponseStatus b = new JsonResponseStatus("ok");
        JsonResponseStatus c = new JsonResponseStatus("error");

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }

    @Test
    void testToString() {
        assertThat(new JsonResponseStatus("ok").toString(), is("JsonResponseStatus{status='ok'}"));
    }
}
