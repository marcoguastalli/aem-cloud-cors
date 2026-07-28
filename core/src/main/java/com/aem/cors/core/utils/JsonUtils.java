package com.aem.cors.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.aem.cors.core.utils.LoggerUtils.logWarnTrackingId;

/**
 * Util class for json operations with the Jackson library
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * Create a String representing the Json of the input Serializable object
     *
     * @param serializable the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown
     */
    public static String createJsonStringFromObject(@NotNull Serializable serializable) {
        try {
            return OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).writeValueAsString(serializable);
        } catch (JsonProcessingException e) {
            log.error("Error createJsonStringFromObject", e);
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
    public static <T> T createObjectFromJsonString(@NotNull String jsonAsString, @NotNull Class<T> clazz) {
        try {
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
    public static ObjectNode createJsonObjectNodeFromEntriesObject(@NotNull Set<Map.Entry<String, @NotNull Map<String, String>>> entries) {
        // create a json
        ObjectNode rootNode = OBJECT_MAPPER.createObjectNode();
        // add input entries
        entries.forEach(entry -> {
            ObjectNode subNode = OBJECT_MAPPER.createObjectNode();
            entry.getValue().forEach(subNode::put);
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

}
