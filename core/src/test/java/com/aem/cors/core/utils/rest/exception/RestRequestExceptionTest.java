package com.aem.cors.core.utils.rest.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

class RestRequestExceptionTest {

    @Test
    void testMessageAndCause() {
        Exception cause = new IllegalStateException("root cause");
        RestRequestException exception = new RestRequestException("something failed", cause);

        assertThat(exception.getMessage(), is("something failed"));
        assertThat(exception.getCause(), sameInstance(cause));
    }
}
