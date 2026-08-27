package com.aem.cors.core.utils.rest.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class MapRestResponseTest {

    @Test
    void testGetMap() {
        Map<String, String> map = Map.of("key", "value");
        MapRestResponse response = new MapRestResponse(map);
        assertThat(response.getMap(), is(map));
    }

    @Test
    void testEqualsAndHashCode() {
        MapRestResponse a = new MapRestResponse(Map.of("key", "value"));
        MapRestResponse b = new MapRestResponse(Map.of("key", "value"));
        MapRestResponse c = new MapRestResponse(Map.of("other", "value"));

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
        assertThat(a.equals("not it"), is(false));
    }

    @Test
    void testToString() {
        MapRestResponse response = new MapRestResponse(Map.of("key", "value"));
        assertThat(response.toString(), is("MapRestResponse{map={key=value}}"));
    }

    @Test
    void testIsAbstractRestResponseSubtype() {
        assertThat(new MapRestResponse(Map.of()) instanceof java.io.Serializable, is(true));
    }
}
