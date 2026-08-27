package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.ArrayComparator.compareSortedArrays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ArrayComparatorTest {

    @Test
    void testEqualArraysDifferentOrder() {
        assertThat(compareSortedArrays(new String[] {"b", "a"}, new String[] {"a", "b"}), is(true));
    }

    @Test
    void testDifferentArrays() {
        assertThat(compareSortedArrays(new String[] {"a", "b"}, new String[] {"a", "c"}), is(false));
    }

    @Test
    void testDifferentLengthArrays() {
        assertThat(compareSortedArrays(new String[] {"a"}, new String[] {"a", "b"}), is(false));
    }

    @Test
    void testFirstArrayNull() {
        assertThat(compareSortedArrays(null, new String[] {"a"}), is(false));
    }

    @Test
    void testSecondArrayNull() {
        assertThat(compareSortedArrays(new String[] {"a"}, null), is(false));
    }

    @Test
    void testBothArraysNull() {
        assertThat(compareSortedArrays(null, null), is(false));
    }
}
