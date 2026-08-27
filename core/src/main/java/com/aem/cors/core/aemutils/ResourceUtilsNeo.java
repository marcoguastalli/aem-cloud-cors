package com.aem.cors.core.aemutils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.aem.cors.core.exceptions.AemRuntimeException;
import com.day.crx.JcrConstants;

/** Utility class for Resource */
public final class ResourceUtilsNeo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtilsNeo.class);
    private static final String FILE_SEPARATOR = "/";
    private static final String PN_TEXT = "text";
    private static final String PN_VALUE = "value";
    private static final String INFO_MSG_DELETED_RESOURCE_WITH_PATH = "Deleted Resource with path '{}'";
    private static final String QUERY_RESOURCE_TYPE = "SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('%1$s') AND [sling:resourceType] = '%2$s'";
    private static final Set<String> BLACKLISTED_PROPERTIES;
    private static final String NODE_NAME_DEFAULT = "default";

    static {
        Set<String> blacklistedProperties = new HashSet<>();
        blacklistedProperties.add("jcr:baseVersion");
        blacklistedProperties.add("jcr:isCheckedOut");
        blacklistedProperties.add("jcr:predecessors");
        blacklistedProperties.add("jcr:uuid");
        blacklistedProperties.add("jcr:versionHistory");
        BLACKLISTED_PROPERTIES = Collections.unmodifiableSet(blacklistedProperties);
    }

    private ResourceUtilsNeo() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Retrieve all the Resource children of the input-path with input-resourceType
     *
     * @param resourceResolver the ResourceResolver
     * @param path the input path
     * @param resourceType the input resourceType
     * @return the result of the query */
    public static Iterator<Resource> getResourcesByResourceType(final ResourceResolver resourceResolver,
            final String path, final String resourceType) {
        final String query = String.format(QUERY_RESOURCE_TYPE, path, resourceType);
        return resourceResolver.findResources(query, Query.JCR_SQL2);
    }

    /** Create the input Resource if it doesn't exists
     *
     * If exists, the existing one is returned
     *
     * @param resourceResolver the ResourceResolver
     * @param parent the new input Resource will be a child of this parentResource
     * @param newResourceName the name of the new resource
     * @param properties the properties of the new resource
     * @return the created Resource, or the existing one
     * @throws PersistenceException if something goes wrong */
    public static Resource createResource(final ResourceResolver resourceResolver, final Resource parent, final String newResourceName,
            final Map<String, Object> properties) throws PersistenceException {
        Resource resource = resourceResolver.getResource(newResourceName);
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            return resource;
        }
        resource = resourceResolver.create(parent, newResourceName, properties);
        resourceResolver.commit();
        LOGGER.info("Created Resource under path '{}' with name '{}'", parent.getPath(), newResourceName);
        return resource;
    }

    /** Copy the input sourcePath Resource in the input targetPath
     *
     * @param resourceResolver the ResourceResolver
     * @param sourcePath the path of the Resource to be copied
     * @param targetPath the path of the Resource to be created
     * @return the created Resource
     * @throws PersistenceException if something goes wrong */
    public static Resource copyResource(final ResourceResolver resourceResolver, final String sourcePath, final String targetPath)
            throws PersistenceException {
        final Resource resource = resourceResolver.copy(sourcePath, targetPath);
        resourceResolver.commit();
        LOGGER.info("Copy Resource from '{}' to '{}'", sourcePath, targetPath);
        return resource;
    }

    /** Delete the Resource at input path
     *
     * @param resourceResolver the ResourceResolver
     * @param path to be deleted
     * @throws PersistenceException if something goes wrong */
    public static void deleteResource(final ResourceResolver resourceResolver, final String path) throws PersistenceException {
        final Resource resource = resourceResolver.resolve(path);
        resourceResolver.delete(resource);
        LOGGER.info("Deleted Resource '{}'", path);
        resourceResolver.commit();
    }

    /** Given the input path and the input properties, get from the Repository the corresponding Resource object
     *
     * If the Resource at the input path does NOT exists, is created with the input properties
     *
     * If the Resource at the input path exists, the existing properties are merged with the input properties
     *
     * If a property already exists, his value is compared, if is NOT equals the existing-property-value is replaced for the
     * input-property-value
     *
     * This method do NOT delete any properties, only add the ones that are not already existing
     *
     * The Resource at the input path is returned with the input properties, or more, if there are any that are not in the input properties
     * object
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path of the Resource to be merged
     * @param properties the properties that has to be merged
     * @return a Resource object instance with the merged input properties added, if not already present
     * @throws PersistenceException if something goes wrong */
    public static Resource mergeResourceProperties(final ResourceResolver resourceResolver, final String path,
            final Map<String, Object> properties) throws PersistenceException {

        // get from the Repository the corresponding Resource object
        Resource resource = resourceResolver.getResource(path);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            final String parentPath = substringBeforeLast(path, FILE_SEPARATOR);
            final Resource parent = resourceResolver.getResource(parentPath);
            if (parent != null) {
                final String newResourceName = substringAfterLast(path, FILE_SEPARATOR);
                return createResource(resourceResolver, parent, newResourceName, properties);
            } else {
                throw new AemRuntimeException("No parente Resource found with path ".concat(parentPath));
            }
        }

        // the existing properties are merged with the input properties
        ModifiableValueMap existingProperties = resource.adaptTo(ModifiableValueMap.class);
        if (existingProperties == null) {
            throw new AemRuntimeException("No properties found with path ".concat(path));
        }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            final String propertyKey = entry.getKey();
            final Object propertyValue = entry.getValue();
            mergeProperty(existingProperties, propertyKey, propertyValue);
        }

        if (resourceResolver.hasChanges()) {
            resourceResolver.commit();
            LOGGER.info("Merged Resource Properties of path '{}'", path);
        }
        return resource;
    }

    /** Return the input path properties as ModifiableValueMap
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path
     * @return a ModifiableValueMap */
    public static @NotNull ModifiableValueMap getModifiableValueMapFromPath(final ResourceResolver resourceResolver, final String path) {
        Resource resource = resourceResolver.getResource(path);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            throw new AemRuntimeException("No Resource found with path: ".concat(path));
        }

        ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        if (modifiableValueMap == null) {
            throw new AemRuntimeException("No properties found with path ".concat(path));
        }
        return modifiableValueMap;
    }

    /** Return the input path properties as ValueMap
     *
     * @param resourceResolver the ResourceResolver
     * @param path the path
     * @return a ValueMap */
    public static ValueMap getValueMapFromPath(final ResourceResolver resourceResolver, final String path) {
        Resource resource = resourceResolver.getResource(path);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            throw new AemRuntimeException("No Resource found with path: ".concat(path));
        }
        return resource.getValueMap();
    }

    /** Delete the input Resource
     *
     * @param resourceResolver the ResourceResolver
     * @param resource the Resource to be deleted */
    public static void deleteResource(final ResourceResolver resourceResolver, final Resource resource) throws PersistenceException {
        resourceResolver.delete(resource);
        LOGGER.info(INFO_MSG_DELETED_RESOURCE_WITH_PATH, resource.getPath());
        resourceResolver.commit();
    }

    /** Delete the input resources from the repository
     *
     * @param resourceResolver the ResourceResolver
     * @param resources a List of Resource to delete */
    public static void deleteResources(final ResourceResolver resourceResolver, final List<Resource> resources) {
        try {
            for (Resource resource : resources) {
                // a resource of the input List can be children of a previous deleted resource == is deleted when the parent is deleted
                if (resourceResolver.getResource(resource.getPath()) != null) {
                    resourceResolver.delete(resource);
                    LOGGER.info(INFO_MSG_DELETED_RESOURCE_WITH_PATH, resource.getPath());
                }
            }
            resourceResolver.commit();
        } catch (PersistenceException e) {
            LOGGER.error("Error delete resources", e);
        }
    }

    /** Merge the input propertyKey and propertyValue with the existingProperties
     *
     * If a property already exists, his value is compared, if is NOT equals the existing-property-value is replaced for the
     * input-property-value
     *
     *
     * @param existingProperties a ModifiableValueMap with the merged properties
     * @param propertyKey the property-key we are comparing
     * @param propertyValue the property-value we are comparing */
    public static void mergeProperty(ModifiableValueMap existingProperties, final String propertyKey, final Object propertyValue) {
        try {
            if (existingProperties.containsKey(propertyKey)) {
                Object existingValue = existingProperties.get(propertyKey);
                if (!Objects.equals(existingValue, propertyValue)) {
                    existingProperties.put(propertyKey, propertyValue);
                }
            } else {
                existingProperties.put(propertyKey, propertyValue);
            }
            LOGGER.info("Merger Properties with key '{}' and value '{}'", propertyKey, propertyValue);
        } catch (Exception e) {
            LOGGER.error("Error merge property: " + propertyKey, e);
        }
    }

    /** Delete all the 1st level children of the input resource
     *
     * @param resourceResolver the ResourceResolver
     * @param resource parentResource
     * @throws PersistenceException if something goes wrong */
    public static void deleteResourceChildren(final ResourceResolver resourceResolver, final Resource resource)
            throws PersistenceException {
        if (resource != null && resource.hasChildren()) {
            final Iterator<Resource> iterator = resource.listChildren();
            while (iterator.hasNext()) {
                Resource resourceToDelete = iterator.next();
                resourceResolver.delete(resourceToDelete);
                LOGGER.info(INFO_MSG_DELETED_RESOURCE_WITH_PATH, resourceToDelete.getPath());
            }
            resourceResolver.commit();
        }
    }

    /** The targetPath is updated with the structure from the corresponding sourcePath.
     *
     * The input node is not modified, in order to maintais his position/order in the JCR
     *
     * The properties of the parent node are not cloned
     *
     * All the children of the targetPath are deleted and re-created copying the children of the sourcePath
     *
     * @param resourceResolver the ResourceResolver
     * @param sourcePath the source path
     * @param targetPath the target path */
    public static void cloneResourceStructure(final ResourceResolver resourceResolver, final String sourcePath, final String targetPath)
            throws PersistenceException {
        final Resource sourceResource = resourceResolver.getResource(sourcePath);
        if (sourceResource == null) {
            throw new IllegalArgumentException(String.format("Cannot create a sourceResource object with path %s", sourcePath));
        }
        Resource targetResource = resourceResolver.getResource(targetPath);
        if (targetResource == null) {
            throw new IllegalArgumentException(String.format("Cannot create a targetResource object with path %s", targetPath));
        }

        // delete all the children of the targetResource
        deleteResourceChildren(resourceResolver, targetResource);
        // get the children of the sourceResource
        final List<Resource> sourceResourceChildren = getResourcesRecursively(sourceResource);
        // exclude sourceResource, maintaining the position/order in the JCR
        final List<Resource> sourceResourceChildrenWithoutFirstResource = sourceResourceChildren.subList(1, sourceResourceChildren.size());

        // need for the API create()
        Resource parent = null;

        // loop over source parsys children
        for (final Resource resourceInSource : sourceResourceChildrenWithoutFirstResource) {
            final String resourceInSourcePath = resourceInSource.getPath();
            final String pathResourceToCreate = StringUtils.replace(resourceInSourcePath, sourcePath, targetPath);
            if (parent == null) {
                // 1st loop, the parent is the targetResource (in this moment has no children)
                parent = targetResource;
            } else {
                parent = resourceResolver.getResource(substringBeforeLast(pathResourceToCreate, FILE_SEPARATOR));
            }
            // create a newResourceInTarget using resourceInSource as source
            final String newResourceInTargetName = substringAfterLast(resourceInSource.getPath(), FILE_SEPARATOR);
            final Resource newResourceInTarget = resourceResolver.create(parent, newResourceInTargetName,
                    filterProperties(resourceInSource.getValueMap()));
            LOGGER.info("Created Resource with path '{}'", newResourceInTarget.getPath());
            if (!"file".equals(newResourceInTargetName) && (!StringUtils.contains(pathResourceToCreate, "file")
                    && !StringUtils.contains(pathResourceToCreate, "file/jcr:content"))) {
                resourceResolver.commit();
            }
        }

        LOGGER.info("Cloned Resource Structure from '{}' to '{}'", sourcePath, targetPath);
    }

    /** Starting from the input resource, retrieves all the sub-resources with the input resourceType
     *
     * @param resource a Resource
     * @param resourceType to search for
     * @return a Collection with filtered Resource, or an empty list */
    public static Collection<Resource> getChildrenWithResourceType(final Resource resource, final String resourceType) {
        return getResourcesRecursively(resource).stream()
                .filter(children -> StringUtils.equals(children.getResourceType(), resourceType))
                .collect(Collectors.toList());
    }

    /** Return the property value of the input Resource as Object, or null if does't exist
     *
     * @param resource a Resource
     * @param propertyName the property we are looking for
     * @return the corresponding value as Object */
    public static Object getObjectPropertyFromResource(final Resource resource, final String propertyName) {
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            throw new AemRuntimeException("No Resource found!");
        }

        final ValueMap valueMap = resource.getValueMap();
        if (valueMap.containsKey(propertyName)) {
            return valueMap.get(propertyName);
        }

        return null;
    }

    /** Return the property value of the input Resource as String, or defaultValue if doesn't exist
     *
     * The method NEVER return NULL
     *
     * @param resourceResolver the ResourceResolver
     * @param path the jcr:content path
     * @param propertyName the name of the property to be retrieved
     * @param defaultValue in case the path doesn't exist, null or exception
     * @return the input propertyName corresponding value at input path, or propertyName */
    public static String getStringPropertyFromResource(final ResourceResolver resourceResolver, final String path,
            final String propertyName, final String defaultValue) {
        String result = defaultValue;
        try {
            result = getValueMapFromPath(resourceResolver, path).get(propertyName, String.class);
        } catch (Exception e) {
            LOGGER.error(String.format("Error get String property '%s' from path '%s'", propertyName, path), e);
        }
        return result != null ? result : defaultValue;
    }

    public static long getNumberOfChildren(final Resource resource) {
        long result = 0L;
        if (!resource.hasChildren()) {
            LOGGER.debug("Resource with path '{}' has no children", resource.getPath());
            return result;
        }
        for (final Resource childLevel1 : resource.getChildren()) {
            if (childLevel1 != null) {
                result++;
            }
        }
        return result;
    }

    /** Update the input propertyName with the input propertyValue of the input resource
     *
     * @param resourceResolver the ResourceResolver
     * @param resource the Resource
     * @param propertyName the property to update
     * @param propertyValue the new value
     * @throws PersistenceException if the commit fails */
    public static void updatePropertyValue(final ResourceResolver resourceResolver, final Resource resource, final String propertyName,
            final Object propertyValue) throws PersistenceException {
        ModifiableValueMap modifiableValueMap = getModifiableValueMapFromPath(resourceResolver, resource.getPath());
        modifiableValueMap.put(propertyName, propertyValue);
        resourceResolver.commit();
    }

    /** Find a parent Resource of the given child Resource by a specific resource type
     *
     * @param child a Resource node
     * @param parentResourceType the resource-type of the parent
     * @return parent resource */
    public static Resource getParentResourceByResourceType(final Resource child, final String parentResourceType) {
        if (child == null) {
            return null;
        }
        final Resource parent = child.getParent();
        if (parent == null) {
            return null;
        }
        if (isResourceOfType(parent, parentResourceType)) {
            return parent;
        }
        return getParentResourceByResourceType(parent, parentResourceType);
    }

    /** Find a parent Resource of the given child Resource by a specific primary type
     *
     * @param child a Resource node
     * @param parentPrimaryType the primary type of the parent
     * @return parent resource */
    public static Resource getParentResourceByPrimaryType(final Resource child, final String parentPrimaryType) {
        final Resource parent = child.getParent();
        if (parent == null) {
            return null;
        }
        if (isResourceOfPrimaryType(parent, parentPrimaryType)) {
            return parent;
        }
        return getParentResourceByPrimaryType(parent, parentPrimaryType);
    }

    /** Check the given resource to be of given type
     *
     * @param resource the Resource
     * @param resType the resource type
     * @return a boolean */
    public static boolean isResourceOfType(final Resource resource, final String resType) {
        if (resource == null) {
            return false;
        }
        if (PageUtilsNeo.IS_PAGE.test(resource)) {
            final Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
            return (contentResource != null) && contentResource.isResourceType(resType);
        }
        return resource.isResourceType(resType);
    }

    /** Check the given resource to be of given primary type
     *
     * @param resource the Resource
     * @param primaryType the resource primary type
     * @return a boolean */
    public static boolean isResourceOfPrimaryType(@NotNull final Resource resource, @NotNull final String primaryType) {
        return primaryType.equals(getPrimaryType(resource));
    }

    /** Returns an unmodifiable list of all the child resources of the given {@code resource} including the {@code resource} itself. If the
     * given {@code resource} is {@code null} the method will return an empty unmodifiable list. If the given {@code resource} is not
     * {@code null} the method will return a unmodifiable list that contains at least the given {@code resource}.
     *
     * @param resource the resource who's children have to be fetched.
     *
     * @return list of children of the given {@code resource} including the given {@code resource} itself or empty list of the given
     *         {@code resource} was null. */
    public static List<Resource> getResourcesRecursively(final Resource resource) {
        if (resource == null) {
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

    /** The resourceType of the personalised component is in the children of the input path Resource
     *
     * @param resourceResolver the ResourceResolver
     * @param path of a Resource with a resourceType = 'cq/personalization/components/target'
     * @return the default subnode resourceType */
    public static String retrievePersonalisedComponentResourceType(final ResourceResolver resourceResolver, final String path) {
        final String pathNodeDefault = path.concat(FILE_SEPARATOR).concat(NODE_NAME_DEFAULT);
        return getStringPropertyFromResource(resourceResolver, pathNodeDefault, SLING_RESOURCE_TYPE_PROPERTY, EMPTY);
    }

    private static String getPrimaryType(final Resource resource) {
        return resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
    }

    private static Map<String, Object> filterProperties(final ValueMap valueMap) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> propertyName : valueMap.entrySet()) {
            final String key = propertyName.getKey();
            if (!BLACKLISTED_PROPERTIES.contains(key)) {
                result.put(key, valueMap.get(key));
            }
        }
        return result;
    }

    /** Given a parent Resource and a child path
     *
     * Build the full path to the child
     *
     * Return the child Resource
     *
     * @param parent a Resource
     * @param path a String
     * @return a Resource, or null */
    public static Resource getChildFromPath(final Resource parent, final String path) {
        if (parent != null && !ResourceUtil.isNonExistingResource(parent)) {
            final String pathToChild = parent.getPath().concat(path);
            ResourceResolver resourceResolver = parent.getResourceResolver();
            return resourceResolver.getResource(pathToChild);
        }
        return null;
    }

    /** Given one or more paths, retrieve the corresponding child Resource that match the input primaryType
     *
     * @param resourceResolver the ResourceResolver
     * @param primaryType used for filtering
     * @param paths as String
     * @return a List of Resources */
    public static List<Resource> getChildrenResourceFromPaths(@NotNull final ResourceResolver resourceResolver,
            @NotNull final String primaryType,
            @NotNull final String... paths) {
        List<Resource> result = new ArrayList<>();
        for (final String path : paths) {
            final Resource resource = resourceResolver.getResource(path);
            if (resource == null) {
                break;
            }
            result.addAll(getResourcesRecursively(resource)
                    .stream()
                    .filter(child -> isResourceOfPrimaryType(child, primaryType))
                    .collect(Collectors.toList()));
        }
        return Collections.unmodifiableList(result);
    }

    /** Builds a Resource that contains the "text" and the "value" property which are the properties that the DataSource expects.
     *
     * @param text The text that will be shown for instance in a select element.
     * @param value The value that will be sent for instance when sending a form with select.
     * @param resourceResolver the ResourceResolver
     * @return SyntheticResource with the expected properties for a Datasource */
    @NotNull
    public static Resource buildResourceForDataSource(final String text, final String value, final ResourceResolver resourceResolver) {
        final ValueMap properties = new ValueMapDecorator(new HashMap<>());
        properties.put(PN_TEXT, StringUtils.defaultIfBlank(text, value));
        properties.put(PN_VALUE, value);
        return new ValueMapResource(
                resourceResolver,
                new ResourceMetadata(),
                com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED,
                properties);
    }

}
