package com.aem.cors.core.utils.rest.exception;

/** Checked exception thrown when a REST request cannot be built or processed */
public class RestRequestException extends Exception {

    private static final long serialVersionUID = -516148162645028250L;

    /** @param message describing the error
     * @param e the underlying cause */
    public RestRequestException(final String message, final Exception e) {
        super(message, e);
    }

}
