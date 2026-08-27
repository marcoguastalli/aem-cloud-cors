package com.aem.cors.core.utils.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

/**
 * Bean used to render in json a org.apache.sling.api.resource.Resource object
 */
public class JsonResource implements Serializable {

    private static final long serialVersionUID = 6835288409186973013L;
    @JsonProperty
    private final String path;
    @JsonProperty
    private final Map<String, Object> valueMap;

    public JsonResource(@NotNull final String path, @NotNull final Map<String, Object> valueMap) {
        this.path = path;
        this.valueMap = valueMap;
    }

    public String getPath() {
        return path;
    }

    public String getPropertyValue(final String propertyName) {
        final Object propertyValue = this.valueMap.get(propertyName);
        if (propertyValue instanceof java.lang.String) {
            return (String) propertyValue;
        }
        return null;
    }

    public Object getPropertyValueObject(final String propertyName) {
        return this.valueMap.get(propertyName);
    }
}
