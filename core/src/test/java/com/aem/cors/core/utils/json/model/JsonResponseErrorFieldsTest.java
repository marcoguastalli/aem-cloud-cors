package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseErrorFieldsTest {

    @Test
    void testGettersAndSetters() {
        JsonResponseErrorMessage message = new JsonResponseErrorMessage(true, false, "T", "M");
        JsonResponseErrorFields fields = new JsonResponseErrorFields("type", "reason", message);

        assertThat(fields.getType(), is("type"));
        assertThat(fields.getReason(), is("reason"));
        assertThat(fields.getMessage(), is(message));

        fields.setType("other-type");
        fields.setReason("other-reason");
        fields.setMessage(null);

        assertThat(fields.getType(), is("other-type"));
        assertThat(fields.getReason(), is("other-reason"));
        assertThat(fields.getMessage(), is((JsonResponseErrorMessage) null));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseErrorMessage message = new JsonResponseErrorMessage(true, false, "T", "M");
        JsonResponseErrorFields a = new JsonResponseErrorFields("type", "reason", message);
        JsonResponseErrorFields b = new JsonResponseErrorFields("type", "reason", message);
        JsonResponseErrorFields c = new JsonResponseErrorFields("other", "reason", message);

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }
}
