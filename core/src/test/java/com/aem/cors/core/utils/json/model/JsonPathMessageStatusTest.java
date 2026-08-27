package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonPathMessageStatusTest {

    @Test
    void testGetters() {
        JsonPathMessageStatus status = new JsonPathMessageStatus("/content/foo", "ok", "success");
        assertThat(status.getPath(), is("/content/foo"));
        assertThat(status.getMessage(), is("ok"));
        assertThat(status.getStatus(), is("success"));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonPathMessageStatus a = new JsonPathMessageStatus("/p", "m", "s");
        JsonPathMessageStatus b = new JsonPathMessageStatus("/p", "m", "s");
        JsonPathMessageStatus c = new JsonPathMessageStatus("/other", "m", "s");

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
        assertThat(a.equals("not it"), is(false));
    }

    @Test
    void testToString() {
        JsonPathMessageStatus status = new JsonPathMessageStatus("/p", "m", "s");
        assertThat(status.toString(), is("{path:'/p', message:'m', status:'s'}"));
    }
}
