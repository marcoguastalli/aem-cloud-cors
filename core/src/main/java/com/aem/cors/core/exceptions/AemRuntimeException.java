package com.aem.cors.core.exceptions;

/**
 * Generic Runtime Exception
 */
public class AemRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -395198879410131753L;

    public AemRuntimeException(String message) {
        super(message);
    }

    public AemRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}