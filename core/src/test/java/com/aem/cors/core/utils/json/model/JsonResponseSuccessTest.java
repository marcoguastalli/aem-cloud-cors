package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseSuccessTest {

    @Test
    void testDefaultConstructor() {
        assertThat(new JsonResponseSuccess().isSuccess(), is(false));
    }

    @Test
    void testIsSuccess() {
        assertThat(new JsonResponseSuccess(true).isSuccess(), is(true));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseSuccess a = new JsonResponseSuccess(true);
        JsonResponseSuccess b = new JsonResponseSuccess(true);
        JsonResponseSuccess c = new JsonResponseSuccess(false);

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }
}
