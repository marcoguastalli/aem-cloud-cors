package com.aem.cors.core.utils.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.aem.cors.core.utils.rest.domain.AbstractRestResponse;

/** Json with GSon utils implementation class */
public final class GsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUtils.class);

    private GsonUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Create a String representing the Json of the input Serializable object
     *
     * @param serializable the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown */
    public static String createGsonStringFromObject(final Serializable serializable) {
        return createGsonStringFromObject(serializable, false);
    }

    /** Create a String representing the Json of the input Serializable object
     *
     * @param serializable the object to be 'jsoned'
     * @param serializeNulls use this option if you want to serialize attributes even if they are null
     * @return a String with the json, or null if an exception is thrown */
    public static String createGsonStringFromObject(final Serializable serializable, final boolean serializeNulls) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            if (serializeNulls) {
                gsonBuilder.serializeNulls();
            }
            Gson gson = gsonBuilder.create();
            return gson.toJson(serializable);
        } catch (Exception e) {
            LOGGER.error("Error createGsonStringFromObject", e);
            return null;
        }
    }

    /** Create a json array with the input List items
     *
     * @param serializable the object to be 'jsoned'
     * @return a String with the json, or null if an exception is thrown */
    public static String createGsonStringFromList(final List<AbstractRestResponse> serializable) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();
            return gson.toJson(serializable, ArrayList.class);
        } catch (Exception e) {
            LOGGER.error("Error createGsonStringFromList", e);
            return null;
        }
    }

}
