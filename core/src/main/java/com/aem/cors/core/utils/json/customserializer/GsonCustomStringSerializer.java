package com.aem.cors.core.utils.json.customserializer;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/*** Gson custom serializer for Strings */
public class GsonCustomStringSerializer implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String value, Type type, JsonSerializationContext jsonSerializationContext) {
        // Find out if the parameter value is a boolean. If it is a boolean cast it to a boolean
        if (Boolean.TRUE.toString().equals(value) || Boolean.FALSE.toString().equals(value)) {
            return new JsonPrimitive(Boolean.valueOf(value));
        } else {
            return new JsonPrimitive(value);
        }
    }
}