package com.aem.cors.core.aemutils;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aem.cors.core.utils.LoggerUtils.logWarnTrackingId;

/**
 * Util class for org.apache.sling.api.resource.Resource
 */
public class ResourceUtils {

    private ResourceUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * If the input path is a valid org.apache.sling.api.resource.Resource object, it is returned
     *
     * @param resourceResolver the ResourceResolver
     * @param path             to get the Resource object from
     * @return a Resource object or null
     */
    public static Resource getResourceFromPath(@NotNull ResourceResolver resourceResolver, @NotNull String path) {
        final Resource resource = resourceResolver.getResource(path);
        if (null != resource && !ResourceUtil.isNonExistingResource(resource)) {
            return resource;
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of all the child resources of the given resource including the resource itself.
     * <p>
     * If the given resource is null the method will return an empty unmodifiable list.
     * <p>
     * If the given resource is not null the method will return a unmodifiable list that contains at least the given resource.
     *
     * @param resource the resource to start from.
     * @return list of Resource
     */
    public static List<Resource> getResourcesRecursively(Resource resource) {
        if (null == resource) {
            return Collections.emptyList();
        }
        final List<Resource> resources = new ArrayList<>();
        // Add the given resource itself to the result list:
        resources.add(resource);
        // Recursively fetch all resources of the given children:
        for (final Resource child : resource.getChildren()) {
            resources.addAll(getResourcesRecursively(child));
        }
        return Collections.unmodifiableList(resources);
    }

    /**
     * Update the input propertyName with the input propertyValue
     *
     * @param resourceResolver the ResourceResolver
     * @param properties       with the properties
     * @param propertyName     to update
     * @param propertyValue    new value
     * @param log              to logger
     * @param trackingId       to track
     */
    public static void updateProperty(@NotNull ResourceResolver resourceResolver, @NotNull ModifiableValueMap properties,
                                      @NotNull String propertyName, @NotNull Object propertyValue, @NotNull Logger log,
                                      @NotNull String trackingId) {
        properties.put(propertyName, propertyValue);
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            logWarnTrackingId(log, trackingId, String.format("Error update property: %s", propertyName));
            resourceResolver.close();
        }
        resourceResolver.close();
    }

    /**
     * @param resourceType to verify
     * @return true if the Resource has resource type RESOURCE_TYPE_NON_EXISTING
     */
    public static boolean isNonExistingResourceType(@NotNull String resourceType) {
        return Resource.RESOURCE_TYPE_NON_EXISTING.equals(resourceType);
    }

    /**
     * Find a parent Resource of the given child Resource by a specific resource type
     *
     * @param child                       a Resource node
     * @param resourceTypeToFindInParents the resource-type of the parent
     * @return parent resource
     */
    public static Resource getParentResourceByResourceType(@Nullable Resource child, @NotNull String resourceTypeToFindInParents) {
        if (child == null) {
            return null;
        }
        final Resource parent = child.getParent();
        if (parent == null) {
            return null;
        }
        if (parent.isResourceType(resourceTypeToFindInParents)) {
            return parent;
        }
        return getParentResourceByResourceType(parent, resourceTypeToFindInParents);
    }

}
