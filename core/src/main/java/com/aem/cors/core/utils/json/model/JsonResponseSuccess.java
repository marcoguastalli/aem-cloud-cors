package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON success object
 */
public class JsonResponseSuccess implements Serializable {

    private static final long serialVersionUID = -6355268264737350609L;
    private boolean success;

    // default empty constructor
    public JsonResponseSuccess() {
    }

    public JsonResponseSuccess(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonResponseSuccess)) {
            return false;
        }
        JsonResponseSuccess that = (JsonResponseSuccess) o;
        return isSuccess() == that.isSuccess();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess());
    }

    @Override
    public String toString() {
        return "{success:" + success + '}';
    }
}
