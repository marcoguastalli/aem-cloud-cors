package com.aem.cors.core.aemutils;

import static com.aem.cors.core.aemutils.ResourceUtilsNeo.createResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.deleteResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getModifiableValueMapFromPath;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getObjectPropertyFromResource;
import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.cors.core.exceptions.AemRuntimeException;
import com.aem.cors.core.utils.ArrayComparator;

/** Utility class for Resource with MultiField */
public final class ResourceMultiValueFieldUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMultiValueFieldUtils.class);
    private static final String ITEM = "item";
    public static final String ERROR_MSG_IT_WAS_NOT_POSSIBLE_TO_ADAPT_RESOURCE_TO_MODIFIABLE_VALUE_MAP = "It was not possible to adapt resource to ModifiableValueMap for resource {}";

    private ResourceMultiValueFieldUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Given the input path and the input propertyName
     *
     * Return the property values as String where the values are separated by the input valuesSeparator
     *
     * @param resourceResolver the ResourceResolver
     * @param path a path
     * @param propertyName a multi-value-field property name
     * @param valuesSeparator the separator of the values in the returned String
     * @param defaultValue in case the path or property doesn't exist, or property is empty
     * @return a String with the values of the multi-field separated by valuesSeparator, or defaultValue */
    public static String getMultiValuePropertyValueAsString(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final String valuesSeparator, final String defaultValue) {
        final String[] propertyValues = getMultiValuePropertyValueAsString(resourceResolver, path, propertyName, new String[0]);
        if (propertyValues.length > 0) {
            return String.join(valuesSeparator, propertyValues);
        }
        return defaultValue;
    }

    /** Given the input path and the input propertyName
     *
     * Return the property values as String[]
     *
     * @param resourceResolver the ResourceResolver
     * @param path a path
     * @param propertyName a multi-value-field property name
     * @param defaultValue in case the path or property doesn't exist, or property is empty
     * @return a String array withe the values of the multi-field, or defaultValue */
    public static String[] getMultiValuePropertyValueAsString(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final String[] defaultValue) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ValueMap properties = resource.getValueMap();
            if (properties.isEmpty()) {
                return defaultValue;
            }
            if (properties.get(propertyName) instanceof String[]) {
                return (String[]) properties.get(propertyName);
            } else {
                LOGGER.debug("The value of property '{}' is not multi value for path '{}'", propertyName, path);
            }
        } else {
            LOGGER.warn("Resource not found:" + path);
        }
        return defaultValue;
    }

    /** Given the input path and the input newValue
     *
     * If the path doesn't contain a multi-value-field propertyName, it is created with the newValue as unique element
     *
     * If the propertyName already exists, the newValue is added as last element of the multi-value-field
     *
     * @param resourceResolver the ResourceResolver
     * @param path a path
     * @param propertyName a multi-value-field property name
     * @param newValue a single value */
    public static void addValueToMultiValueField(final ResourceResolver resourceResolver, final String path, final String propertyName,
            final String newValue) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
            if (modifiableValueMap != null) {
                final String[] currentValues = (String[]) modifiableValueMap.get(propertyName);
                if (currentValues == null) {
                    final String[] newValues = { newValue };
                    modifiableValueMap.put(propertyName, newValues);
                } else {
                    List<String> currentValuesAsList = new LinkedList<>(Arrays.asList(currentValues));
                    currentValuesAsList.add(newValue);
                    final String[] newValues = currentValuesAsList.toArray(new String[0]);
                    modifiableValueMap.put(propertyName, newValues);
                }
            } else {
                LOGGER.error(ERROR_MSG_IT_WAS_NOT_POSSIBLE_TO_ADAPT_RESOURCE_TO_MODIFIABLE_VALUE_MAP, resource.getPath());
            }
        } else {
            LOGGER.error("Resource not found:" + path);
        }
    }

    /** Given the input path and the input valueToRemove
     *
     * If the path contains a multi-value-field propertyName, the currentValues are retrieved
     *
     * The valueToRemove is removed from the currentValues
     *
     * If the currentValues (after the remove of valueToRemove) isEmpty the whole propertyName is removed
     *
     * else the values are updated
     *
     * @param resourceResolver the ResourceResolver
     * @param path a path
     * @param propertyName a multi-value-field property name
     * @param valueToRemove a single value */
    public static void removeValueFromMultiValueField(final ResourceResolver resourceResolver, final String path, final String propertyName,
            final String valueToRemove) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
            if (modifiableValueMap != null) {
                final String[] currentValues = (String[]) modifiableValueMap.get(propertyName);
                if (currentValues != null) {
                    List<String> currentValuesAsList = new LinkedList<>(Arrays.asList(currentValues));
                    currentValuesAsList.remove(valueToRemove);
                    if (currentValuesAsList.isEmpty()) {
                        // if was removed the unique value, the whole property is removed
                        modifiableValueMap.remove(propertyName);
                    } else {
                        final String[] newValues = currentValuesAsList.toArray(new String[0]);
                        modifiableValueMap.put(propertyName, newValues);
                    }
                }
            } else {
                LOGGER.error(ERROR_MSG_IT_WAS_NOT_POSSIBLE_TO_ADAPT_RESOURCE_TO_MODIFIABLE_VALUE_MAP, resource.getPath());
            }
        } else {
            LOGGER.error("Resource not found:" + path);
        }
    }

    /** Given the input path and the input valueToCheck
     *
     * If the path contains a multi-value-field propertyName, the currentValues are retrieved
     *
     * If the currentValues are null, it is created with the valueToCheck as unique element
     *
     * Else the valueToCheck is checked in the currentValues, and added if not present
     *
     * @param resourceResolver the ResourceResolver
     * @param path a path
     * @param propertyName a multi-value-field property name
     * @param valueToCheck a single value */
    public static void checkValueInMultiValueField(final ResourceResolver resourceResolver, final String path, final String propertyName,
            final String valueToCheck) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
            if (modifiableValueMap != null) {
                final String[] currentValues = (String[]) modifiableValueMap.get(propertyName);
                if (currentValues == null) {
                    final String[] newValues = { valueToCheck };
                    modifiableValueMap.put(propertyName, newValues);
                } else {
                    List<String> currentValuesAsList = new LinkedList<>(Arrays.asList(currentValues));
                    if (!currentValuesAsList.contains(valueToCheck)) {
                        currentValuesAsList.add(valueToCheck);
                        final String[] newValues = currentValuesAsList.toArray(new String[0]);
                        modifiableValueMap.put(propertyName, newValues);
                    }
                }
            } else {
                LOGGER.error(ERROR_MSG_IT_WAS_NOT_POSSIBLE_TO_ADAPT_RESOURCE_TO_MODIFIABLE_VALUE_MAP, resource.getPath());
            }
        } else {
            LOGGER.error("Resource not found:" + path);
        }
    }

    /** Reset the input propertyName of the input path
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path
     * @param propertyName the property to be reset
     * @return the Resource updated
     * @throws PersistenceException if something goes wrong */
    public static Resource resetMultiValueFieldProperty(final ResourceResolver resourceResolver, final String path,
            final String propertyName) throws PersistenceException {
        ModifiableValueMap modifiableValueMap = getModifiableValueMapFromPath(resourceResolver, path);
        modifiableValueMap.put(propertyName, new String[0]);
        resourceResolver.commit();
        LOGGER.info("Resetted Resource Multi Value Property '{}' of path '{}'", propertyName, path);
        return resourceResolver.getResource(path);
    }

    /** Add the input newPropertyValues at the input propertyName, of the input path
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path
     * @param propertyName the property to be merged
     * @param inputValuesList a List of new property-values to be added to the existingValues
     * @return the Resource updated
     * @throws PersistenceException if something goes wrong */
    public static Resource mergeResourceMultiValueFieldProperty(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final List<String> inputValuesList) throws PersistenceException {
        ModifiableValueMap modifiableValueMap = getModifiableValueMapFromPath(resourceResolver, path);

        String[] existingValues = (String[]) modifiableValueMap.get(propertyName);
        if (ArrayUtils.isEmpty(existingValues)) {
            // remove duplicated
            final List<String> filteredList = inputValuesList.stream().distinct().collect(Collectors.toList());
            // put
            modifiableValueMap.put(propertyName, filteredList.toArray(new String[0]));
        } else {
            List<String> existingValuesInMultiFieldPropertyList = new ArrayList<>(Arrays.asList(existingValues));
            existingValuesInMultiFieldPropertyList.addAll(inputValuesList);
            // remove duplicated
            final List<String> filteredList = existingValuesInMultiFieldPropertyList.stream().distinct().collect(Collectors.toList());
            // put
            modifiableValueMap.put(propertyName, filteredList.toArray(new String[0]));
        }

        resourceResolver.commit();
        LOGGER.info("Merged Resource Multi Value Property '{}' of path '{}'", propertyName, path);
        return resourceResolver.getResource(path);
    }

    /** Replace input propertyName values of the input path
     *
     * The input inputValuesList will replace the existingValues only if they're different
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path
     * @param propertyName the property to be replaced
     * @param inputValuesList the new property values to be added
     * @return true if the propertyName existingValues were replaced by the inputValuesList, false if nothing changed
     * @throws PersistenceException if something goes wrong */
    public static boolean replaceResourceMultiValueFieldProperty(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final List<String> inputValuesList) throws PersistenceException {
        ModifiableValueMap modifiableValueMap = getModifiableValueMapFromPath(resourceResolver, path);

        final String[] existingValues = (String[]) modifiableValueMap.get(propertyName);
        final String[] inputValues = inputValuesList.toArray(new String[0]);
        if (!ArrayComparator.compareSortedArrays(existingValues, inputValues)) {
            // Prevent java.lang.IllegalArgumentException: Value for key propertyName can't be put into node
            if (!PageUtilsNeo.isResourceOfTypePage(resourceResolver, path)) {
                modifiableValueMap.put(propertyName, inputValues);
            }
        }

        // save only if any propertyName has changed
        if (resourceResolver.hasChanges()) {
            resourceResolver.commit();
            LOGGER.info("Replaced Resource Multi Value Property '{}' of path '{}'", propertyName, path);
            return true;
        }
        return false;
    }

    /** If the input Resource contains the input parameterName property
     *
     * The value is replaced with the input parameterValue, only if is !isEmpty
     *
     * If the value isEmpty the parameter is removed from the Resource
     *
     * @param resourceResolver the ResourceResolver
     * @param resource the Resource
     * @param parameterName the name of the parameter
     * @param parameterValue the value of the parameter
     * @throws Exception if commit fails */
    public static void saveOrReplaceMultiValueParameterInResource(final ResourceResolver resourceResolver, Resource resource,
            final String parameterName, final String[] parameterValue) throws Exception {
        try {
            ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
            if (modifiableValueMap != null && !modifiableValueMap.isEmpty()) {
                if (modifiableValueMap.get(parameterName) != null) {
                    // remove old value (every time is present)
                    modifiableValueMap.remove(parameterName);
                    resourceResolver.commit();
                }
                if (!ArrayUtils.isEmpty(parameterValue)) {
                    // replace() from API throws an Exception if the previous value is not multi-value
                    modifiableValueMap.put(parameterName, parameterValue);
                    resourceResolver.commit();
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error commit parameter '%s' property in Resource '%s'", parameterName, resource.getPath()), e);
            throw e;
        }
    }

    /** Given the structure: component-with-multi-field / field-name / itemX
     *
     * where itemX contains a single value property (propertyName), e.g: reference
     *
     * Returns a List of all the input propertyName values ordered as in inserted in the dialog
     *
     * @param resourceResolver the ResourceResolver
     * @param path of the component with multi-field
     * @param propertyName the property name
     * @return a List of all the input propertyName values */
    public static List<String> getMultiValueFieldPropertyValues(final ResourceResolver resourceResolver, final String path,
            final String propertyName) {
        List<String> result = new LinkedList<>();
        final Resource resource = resourceResolver.getResource(path);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            throw new AemRuntimeException("No Resource found with path: ".concat(path));
        }
        if (!resource.hasChildren()) {
            LOGGER.debug("Resource with path '{}' has no children", resource.getPath());
            return result;
        }

        final Iterator<Resource> childrenLevel1 = resource.getChildren().iterator();
        for (Iterator<Resource> iteratorLevel1 = childrenLevel1; childrenLevel1.hasNext();) {
            final Resource childLevel1 = iteratorLevel1.next();
            if (!childLevel1.hasChildren()) {
                LOGGER.debug("Resource with path '{}' has no children", resource.getPath());
                return result;
            }
            final Iterator<Resource> childrenLevel2 = childLevel1.getChildren().iterator();
            for (Iterator<Resource> iteratorLevel2 = childrenLevel2; childrenLevel2.hasNext();) {
                final Resource childLevel2 = iteratorLevel2.next();
                final Object propertyValue = getObjectPropertyFromResource(childLevel2, propertyName);
                if (propertyValue != null) {
                    result.add(String.valueOf(propertyValue));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Given the structure: component-with-multi-field / field-name / itemX
     *
     * where itemX contains a single value property (propertyName), e.g: reference
     *
     * Retrieve the field-name Resource and delete all the children (itemX)
     *
     * Creates new Resource itemX many as newPropertyValues
     *
     * @param resourceResolver the ResourceResolver
     * @param path of the component with multi-field
     * @param propertyName the property name
     * @param newPropertyValues the new values for the propertyName in itemX */
    public static void setMultiValueFieldChildren(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final List<String> newPropertyValues) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            throw new AemRuntimeException("No Resource found with path: ".concat(path));
        }
        if (!resource.hasChildren()) {
            LOGGER.debug("Resource with path '{}' has no children", resource.getPath());
            return;
        }

        final String[] newValues = newPropertyValues.toArray(new String[0]);

        try {
            final Iterator<Resource> childrenLevel1 = resource.getChildren().iterator();
            for (; childrenLevel1.hasNext();) {
                final Resource childLevel1 = childrenLevel1.next();
                if (!childLevel1.hasChildren()) {
                    LOGGER.debug("Resource with path '{}' has no children", resource.getPath());
                    return;
                }
                // delete resources itemX
                final Iterator<Resource> childrenLevel2 = childLevel1.getChildren().iterator();
                for (; childrenLevel2.hasNext();) {
                    final Resource childLevel2 = childrenLevel2.next();
                    deleteResource(resourceResolver, childLevel2.getPath());
                }
                // create resources itemX
                for (int i = 0; i < newValues.length; i++) {
                    final String newChildrenLevel2Path = ITEM + i;
                    final String newValue = newValues[i];
                    final Map<String, Object> properties = createMultiValueFieldProperties(propertyName, newValue);
                    createResource(resourceResolver, childLevel1, newChildrenLevel2Path, properties);
                }
            }

            if (newValues.length == 0) {
                // if there are no nodes itemX the whole component is removed
                resourceResolver.delete(resource);
            }

            resourceResolver.commit();
        } catch (PersistenceException e) {
            resourceResolver.revert();
            throw new AemRuntimeException(String.format("Error set property '%s' in path '%s'", propertyName, path), e);
        }
    }

    /** Creates the itemX properties
     *
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return a Map<String, Object> with the new properties */
    private static Map<String, Object> createMultiValueFieldProperties(final String propertyName, final String propertyValue) {
        Map<String, Object> result = new HashMap<>();
        result.put(JCR_PRIMARYTYPE, NT_UNSTRUCTURED);
        result.put(propertyName, propertyValue);
        return result;
    }

}
