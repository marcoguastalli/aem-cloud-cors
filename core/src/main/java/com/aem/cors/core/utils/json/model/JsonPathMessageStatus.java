package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON success path message
 */
public class JsonPathMessageStatus implements Serializable {
    private static final long serialVersionUID = 6363314757709407183L;
    private String path;
    private String message;
    private String status;

    // default empty constructor
    public JsonPathMessageStatus() {
    }

    public JsonPathMessageStatus(final String path, final String message, final String status) {
        this.path = path;
        this.message = message;
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonPathMessageStatus)) {
            return false;
        }
        JsonPathMessageStatus that = (JsonPathMessageStatus) o;
        return Objects.equals(getPath(), that.getPath()) &&
            Objects.equals(getMessage(), that.getMessage()) &&
            Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), getMessage(), getStatus());
    }

    @Override
    public String toString() {
        return "{path:'" + path + '\'' +
            ", message:'" + message + '\'' +
            ", status:'" + status + '\'' +
            '}';
    }
}
