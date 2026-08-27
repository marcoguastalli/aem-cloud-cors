package com.aem.cors.core.aemutils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.aem.cors.core.utils.LoggerUtils.logWarnTrackingId;

/**
 * Util class for json operations with the Jackson library.
 *
 * <p>Merged from two prior implementations of a {@code JsonJacksonUtils} class that both
 * defined overlapping methods (some marked {@code // from alternate source}). Where both
 * defined the same method signature
 * ({@code createJsonStringFromObject(Serializable)}, {@code createObjectFromJsonString(String, Class)}),
 * only one implementation is kept as it was equivalent or strictly more capable.</p>
 */
@Slf4j
public class JsonJacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonJacksonUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * Create a String representing the Json of the input Serializable object
     *
     * @param serializable the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown
     */
    // A simpler equivalent duplicate of this method existed in one of the merged sources; dropped.
    public static String createJsonStringFromObject(@NotNull Serializable serializable) {
        try {
            return OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).writeValueAsString(serializable);
        } catch (JsonProcessingException e) {
            log.error("Error createJsonStringFromObject", e);
            return null;
        }
    }

    /**
     * Create a String representing the Json of the input Serializable object
     *
     * @param serializable the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown
     */
    public static String createJsonStringFromObjectIgnoreNull(@NotNull Serializable serializable) {
        try {
            return OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .writeValueAsString(serializable);
        } catch (JsonProcessingException e) {
            log.error("Error createJsonStringFromObjectIgnoreNull", e);
            return null;
        }
    }

    /**
     * Create a String representing the Json of the input Object
     *
     * @param object the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown
     */
    public static String createJsonStringFromObject(@NotNull Object object) {
        try {
            return OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error createJsonStringFromObject", e);
            return null;
        }
    }

    /**
     * Create a String representing the Json of the List of objects
     *
     * @param list list of objects
     * @param <T>  Java generics T
     * @return Json String or null if an exception is thrown
     */
    public static <T> String createJsonStringFromList(@NotNull List<T> list) {
        try {
            return OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Error createJsonFromSerializableList", e);
            return null;
        }
    }

    /**
     * Create an instance of the object from the input string and class
     *
     * @param jsonAsString the string to be converted to a json
     * @param clazz        the type of the class to be created
     * @return a Serializable instance object
     */
    // A raw-Class/Object-return duplicate of this method existed in one of the merged sources; the
    // generic version below is strictly more capable (String short-circuit + typed return), so
    // that duplicate was dropped.
    public static <T> T createObjectFromJsonString(@NotNull String jsonAsString, @NotNull Class<T> clazz) {
        try {
            if (clazz.equals(String.class)) {
                return clazz.cast(jsonAsString);
            }
            return OBJECT_MAPPER.readValue(jsonAsString, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error createObjectFromJsonString", e);
            return null;
        }
    }

    /**
     * Create a json with the input entries
     *
     * @param entries to add in the json
     * @return an ObjectNode
     */
    public static ObjectNode createJsonObjectNodeFromEntries(@NotNull Set<Map.Entry<String, String>> entries) {
        // create a json
        ObjectNode rootNode = OBJECT_MAPPER.createObjectNode();
        entries.forEach(entry -> {
            // create a node
            final TextNode textNode = new TextNode(entry.getValue());
            // add the node to the json
            rootNode.putIfAbsent(entry.getKey(), textNode);
        });
        return rootNode;
    }

    /**
     * Create a json with the input entries
     *
     * @param entries to add in the json
     * @return an ObjectNode
     */
    public static ObjectNode createJsonObjectNodeFromEntriesObject(@NotNull Set<Map.Entry<String, Map<String, Object>>> entries) {
        // create a json
        ObjectNode rootNode = OBJECT_MAPPER.createObjectNode();
        // add input entries
        entries.forEach(entry -> {
            ObjectNode subNode = OBJECT_MAPPER.createObjectNode();
            entry.getValue().forEach((key, value) -> {
                if (value instanceof String) {
                    subNode.put(key, (String) value);
                } else if (value instanceof Boolean) {
                    subNode.put(key, (Boolean) value);
                }
            });
            rootNode.set(entry.getKey(), subNode);
        });
        return rootNode;
    }

    /**
     * Create a json array with the input entries
     *
     * @param entries to add in the json array
     * @return an ArrayNode
     */
    public static ArrayNode createJsonArrayNodeFromEntries(@NotNull Set<Map.Entry<String, String>> entries) {
        // create a json array
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        entries.forEach(entry -> {
            // create a node
            final ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
            final TextNode textNode = new TextNode(entry.getValue());
            // add the node to the json
            objectNode.putIfAbsent(entry.getKey(), textNode);
            arrayNode.add(objectNode);
        });
        return arrayNode;
    }

    /**
     * Create a json array with the input entries
     *
     * @param serializableList a List of objects to add to the json
     * @return an ArrayNode
     */
    public static ArrayNode createJsonArrayNodeFromObjects(@NotNull List<Serializable> serializableList) {
        // create a json array
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        //add the input entries
        serializableList.forEach(arrayNode::addPOJO);
        return arrayNode;
    }

    /**
     * @param slingHttpServletRequest the Request
     * @param trackingId              to track the user
     * @param clazz                   type class to convert json
     * @param <T>                     Java generics T
     * @return the corresponding clazz json object from the input Request
     */
    public static <T> T getObjectFromRequest(@NotNull SlingHttpServletRequest slingHttpServletRequest, @NotNull String trackingId, @NotNull Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(slingHttpServletRequest.getInputStream(), clazz);
        } catch (IllegalStateException | IOException e) {
            final String errorMessage = "Cannot create a json with the provided input";
            logWarnTrackingId(log, trackingId, errorMessage);
        }
        return null;
    }

    /**
     * @param inputStream the input Stream
     * @param trackingId  to track the user
     * @param clazz       type class to convert json
     * @param <T>         Java generics T
     * @return the corresponding clazz json object from the input Request
     */
    public static <T> T getObjectFromInputStream(@NotNull InputStream inputStream, @NotNull String trackingId, @NotNull Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (IllegalStateException | IOException e) {
            final String errorMessage = "Cannot create a json with the provided input";
            logWarnTrackingId(log, trackingId, errorMessage);
        }
        return null;
    }

    /**
     * Create JsonNode from json String
     *
     * @param json       The string to be converted into JsonNode
     * @param trackingId to track the user
     * @return JsonNode Object
     */
    public static JsonNode getJsonNodeFromString(@NotNull String json, @NotNull String trackingId) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            logWarnTrackingId(log, trackingId, json);
        }
        return null;
    }

    /**
     * @param json       The string to be converted into List of object
     * @param trackingId to log
     * @param clazz      type class to convert json
     * @param <T>        as Type
     * @return List of clazz
     */
    public static <T> List<T> createListFromJsonString(@NotNull String json, @NotNull String trackingId, @NotNull Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            logWarnTrackingId(log, trackingId, e.getMessage());
        }
        return null;
    }

    /**
     * @param trackingId to log
     * @param clazz      type class to convert json
     * @param obj        as Object
     * @param <T>        as Type
     * @return the corresponding type object
     */
    public static <T> T createObject(@NotNull String trackingId, @NotNull Class<T> clazz, @NotNull Object obj) {
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), clazz);
        } catch (JsonProcessingException e) {
            logWarnTrackingId(log, trackingId, e.getMessage());
        }
        return null;
    }

    /**
     * Create a JsonNode object from the input string.
     *
     * @param jsonAsString the string to be converted to a json
     * @return a JsonNode with the result, or null if something goes wrong
     */
    public static JsonNode createJsonNodeFromJsonString(final String jsonAsString) {
        try {
            return OBJECT_MAPPER.readTree(jsonAsString);
        } catch (IOException e) {
            log.error("Error createJsonNodeFromJsonString", e);
            return null;
        }
    }

    /**
     * Create an instance of the object from the input Reader and class.
     *
     * @param reader the reader that contains the JSON
     * @param clazz  the type of the class to be created
     * @return a Serializable instance object
     */
    public static <T> T createInstanceFromReader(final Reader reader, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(reader, clazz);
        } catch (IOException e) {
            log.error("Error createInstanceFromReader", e);
            return null;
        }
    }
}
