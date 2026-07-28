package com.aem.cors.core.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AemRuntimeExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Test error message";
        AemRuntimeException exception = new AemRuntimeException(message);

        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), is(message));
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test error with cause";
        RuntimeException cause = new RuntimeException("Root cause");
        AemRuntimeException exception = new AemRuntimeException(message, cause);

        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), is(message));
        assertThat(exception.getCause(), is(cause));
    }

    @Test
    void testThrowAemRuntimeException() {
        assertThrows(AemRuntimeException.class, () -> {
            throw new AemRuntimeException("Intentional error");
        });
    }

    @Test
    void testAemRuntimeExceptionIsRuntimeException() {
        AemRuntimeException exception = new AemRuntimeException("Test");
        assertThat(exception instanceof RuntimeException, is(true));
    }
}
