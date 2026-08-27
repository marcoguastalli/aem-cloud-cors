package com.aem.cors.core.utils.json.customserializer;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class GsonCustomStringSerializerTest {

    private final GsonCustomStringSerializer serializer = new GsonCustomStringSerializer();

    @Test
    void testSerializeTrueString() {
        JsonElement element = serializer.serialize("true", String.class, null);
        assertThat(element.getAsBoolean(), is(true));
        assertThat(element.getAsJsonPrimitive().isBoolean(), is(true));
    }

    @Test
    void testSerializeFalseString() {
        JsonElement element = serializer.serialize("false", String.class, null);
        assertThat(element.getAsBoolean(), is(false));
        assertThat(element.getAsJsonPrimitive().isBoolean(), is(true));
    }

    @Test
    void testSerializeRegularString() {
        JsonElement element = serializer.serialize("hello", String.class, null);
        assertThat(element.getAsJsonPrimitive().isString(), is(true));
        assertThat(element.getAsString(), is("hello"));
    }
}
