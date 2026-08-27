package com.aem.cors.core.utils.rest.domain;

import java.io.Serializable;

/** Base class for REST response domain objects; marks subclasses as Serializable for JSON (de)serialization */
public abstract class AbstractRestResponse implements Serializable {
    private static final long serialVersionUID = -6694994456704032288L;
}
