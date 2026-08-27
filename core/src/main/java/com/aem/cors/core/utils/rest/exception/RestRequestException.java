package com.aem.cors.core.utils.rest.exception;

public class RestRequestException extends Exception {

    private static final long serialVersionUID = -516148162645028250L;

    public RestRequestException(final String message, final Exception e) {
        super(message, e);
    }

}
