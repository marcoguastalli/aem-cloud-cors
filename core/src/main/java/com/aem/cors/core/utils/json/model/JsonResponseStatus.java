package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON status object
 */
public class JsonResponseStatus implements Serializable {

    private static final long serialVersionUID = -2129747089138189545L;
    private String status;

    // default empty constructor
    public JsonResponseStatus() {
    }

    public JsonResponseStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonResponseStatus)) {
            return false;
        }
        JsonResponseStatus that = (JsonResponseStatus) o;
        return Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus());
    }

    @Override
    public String toString() {
        return "JsonResponseStatus{" +
            "status='" + status + '\'' +
            '}';
    }
}
