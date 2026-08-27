package com.aem.cors.core.aemutils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/** Utility class for Properties */
public class PropertiesUtils {

    private PropertiesUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Check if the input resource contains the input propertyName
     *
     * @param resource a Resource
     * @param propertyName the property to be checked
     * @return true or false */
    public static boolean resourceHasProperty(final Resource resource, final String propertyName) {
        return isNotBlank(resource.getValueMap().get(propertyName, String.class));
    }

    /** Given a Resource and two property names, checks:
     * 
     * - if primaryProperty exists, the primaryProperty is returned
     * 
     * - if primaryProperty does NOT exist, tries returning fallbackProperty
     * 
     * @param resource a Resource
     * @param primaryProperty the property name with priority 1
     * @param fallbackProperty the property name with priority 2
     * @return a String with the property value */
    public static String getPropertyOrFallback(final Resource resource, final String primaryProperty,
            final String fallbackProperty) {
        boolean resourceHasPrimaryProperty = resourceHasProperty(resource, primaryProperty);
        if (resourceHasPrimaryProperty) {
            return resource.getValueMap().get(primaryProperty, String.class);
        }
        return resource.getValueMap().get(fallbackProperty, String.class);
    }

    /** Given valueMap and propertyName returns the corresponding value, if exist
     * 
     * @param valueMap a ValueMap
     * @param propertyName a property name
     * @param clazz of the property
     * @return an Optional with the property value
     * @param <T> the property type */
    public static <T> Optional<T> getPropertiesValueIfExist(final ValueMap valueMap, @NotNull final String propertyName,
            @NotNull final Class<T> clazz) {
        return Optional.ofNullable(valueMap.get(propertyName, clazz));
    }

}
