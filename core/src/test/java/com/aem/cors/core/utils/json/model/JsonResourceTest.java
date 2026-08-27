package com.aem.cors.core.utils.json.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class JsonResourceTest {

    @Test
    void testGetPath() {
        JsonResource resource = new JsonResource("/content/foo", Map.of("title", "Foo"));
        assertThat(resource.getPath(), is("/content/foo"));
    }

    @Test
    void testGetPropertyValueString() {
        JsonResource resource = new JsonResource("/content/foo", Map.of("title", "Foo"));
        assertThat(resource.getPropertyValue("title"), is("Foo"));
    }

    @Test
    void testGetPropertyValueNonString() {
        JsonResource resource = new JsonResource("/content/foo", Map.of("count", 5));
        assertThat(resource.getPropertyValue("count"), nullValue());
    }

    @Test
    void testGetPropertyValueObject() {
        JsonResource resource = new JsonResource("/content/foo", Map.of("count", 5));
        assertThat(resource.getPropertyValueObject("count"), is(5));
    }

    @Test
    void testGetPropertyValueMissing() {
        JsonResource resource = new JsonResource("/content/foo", Map.of());
        assertThat(resource.getPropertyValue("missing"), nullValue());
    }
}
