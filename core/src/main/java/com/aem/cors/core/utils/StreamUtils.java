package com.aem.cors.core.utils;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Helper class to convert various types to Java 8 streams */
public class StreamUtils {

    private StreamUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param iterator a iterator instance object
     * @param <T> the generic class
     * @return the given iterator as stream */
    public static <T> Stream<T> toStream(final Iterator<T> iterator) {
        return toStream(() -> iterator);
    }

    /** @param iterable a iterable instance object
     * @param <T> the generic class
     * @return the given iterable as stream */
    public static <T> Stream<T> toStream(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

}
