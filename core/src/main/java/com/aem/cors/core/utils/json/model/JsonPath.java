package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/** Representation of a JSON object holding a single 'path' property */
public class JsonPath implements Serializable {
    private static final long serialVersionUID = 8538601591011347749L;
    private String path;

    // default empty constructor
    public JsonPath() {
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonPath)) {
            return false;
        }
        JsonPath jsonPath = (JsonPath) o;
        return Objects.equals(getPath(), jsonPath.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }

    @Override
    public String toString() {
        return "{path: '" + path + '\'' + '}';
    }
}
