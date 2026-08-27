package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON result object
 */
public class JsonResponseResult implements Serializable {

    private static final long serialVersionUID = -9023356794723503411L;
    private Object result;

    /**
     * default constructor for json purposes
     */
    public JsonResponseResult() {
    }

    public JsonResponseResult(final Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonResponseResult)) {
            return false;
        }
        JsonResponseResult that = (JsonResponseResult) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        return "{\"result\":" + this.result + "}";
    }
}
