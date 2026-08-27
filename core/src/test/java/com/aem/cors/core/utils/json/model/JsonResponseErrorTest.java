package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseErrorTest {

    @Test
    void testGetterAndSetter() {
        JsonResponseErrorFields fields = new JsonResponseErrorFields("type", "reason", null);
        JsonResponseError error = new JsonResponseError(fields);

        assertThat(error.getError(), is(fields));

        JsonResponseErrorFields other = new JsonResponseErrorFields("other", "reason", null);
        error.setError(other);
        assertThat(error.getError(), is(other));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseErrorFields fields = new JsonResponseErrorFields("type", "reason", null);
        JsonResponseError a = new JsonResponseError(fields);
        JsonResponseError b = new JsonResponseError(fields);
        JsonResponseError c = new JsonResponseError(new JsonResponseErrorFields("other", "reason", null));

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }
}
