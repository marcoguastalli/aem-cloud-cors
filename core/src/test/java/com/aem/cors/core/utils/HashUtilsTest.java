package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.HashUtils.decodeStringFromBase64;
import static com.aem.cors.core.utils.HashUtils.getStringHash;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class HashUtilsTest {

    @Test
    void testGetStringHash() {
        String result = getStringHash("hello");
        assertThat(result, is("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d"));
    }

    @Test
    void testGetStringHashIsDeterministic() {
        assertThat(getStringHash("same input"), is(getStringHash("same input")));
    }

    @Test
    void testDecodeStringFromBase64() {
        assertThat(decodeStringFromBase64("aGVsbG8="), is("hello"));
    }
}
