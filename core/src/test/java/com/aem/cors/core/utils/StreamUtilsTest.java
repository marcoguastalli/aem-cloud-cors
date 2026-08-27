package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.aem.cors.core.utils.StreamUtils.toStream;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StreamUtilsTest {

    @Test
    void testToStreamFromIterator() {
        Iterator<String> iterator = List.of("a", "b", "c").iterator();
        List<String> result = toStream(iterator).collect(Collectors.toList());
        assertThat(result, is(List.of("a", "b", "c")));
    }

    @Test
    void testToStreamFromIterable() {
        Iterable<String> iterable = List.of("x", "y");
        List<String> result = toStream(iterable).collect(Collectors.toList());
        assertThat(result, is(List.of("x", "y")));
    }
}
