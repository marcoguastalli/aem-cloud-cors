package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON error result object
 */
public class JsonResponseErrorFields implements Serializable {

    private static final long serialVersionUID = -2299840154953872177L;
    private String type;
    private String reason;
    private JsonResponseErrorMessage message;

    public JsonResponseErrorFields(final String type, final String reason, final JsonResponseErrorMessage message) {
        this.type = type;
        this.reason = reason;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public JsonResponseErrorMessage getMessage() {
        return message;
    }

    public void setMessage(JsonResponseErrorMessage message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonResponseErrorFields)) {
            return false;
        }
        JsonResponseErrorFields that = (JsonResponseErrorFields) o;
        return Objects.equals(type, that.type) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, reason, message);
    }

    @Override
    public String toString() {
        return "{type='" + type + '\'' +
            ", reason='" + reason + '\'' +
            ", message=" + message +
            '}';
    }
}
