package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResponseErrorMessageTest {

    @Test
    void testGettersAndSetters() {
        JsonResponseErrorMessage message = new JsonResponseErrorMessage(true, false, "Title", "Message");
        assertThat(message.isTranslated(), is(true));
        assertThat(message.isFullKey(), is(false));
        assertThat(message.getTitle(), is("Title"));
        assertThat(message.getMessage(), is("Message"));

        message.setTranslated(false);
        message.setFullKey(true);
        message.setTitle("New Title");
        message.setMessage("New Message");

        assertThat(message.isTranslated(), is(false));
        assertThat(message.isFullKey(), is(true));
        assertThat(message.getTitle(), is("New Title"));
        assertThat(message.getMessage(), is("New Message"));
    }

    @Test
    void testEqualsAndHashCode() {
        JsonResponseErrorMessage a = new JsonResponseErrorMessage(true, false, "T", "M");
        JsonResponseErrorMessage b = new JsonResponseErrorMessage(true, false, "T", "M");
        JsonResponseErrorMessage c = new JsonResponseErrorMessage(false, false, "T", "M");

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
    }
}
