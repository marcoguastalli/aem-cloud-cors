package com.aem.cors.core.aemutils.page;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.aem.cors.core.aemutils.ResourceUtilsNeo;

public final class PageOperationsUtils {

    private PageOperationsUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param resource a Resource
     * @return true if the input Resource has resource-type cq:Page, false instead */
    public static boolean isResourceTypePage(final Resource resource) {
        return resource.getResourceType().equals(NT_PAGE);
    }

    /** @param resourceResolver the ResourceResolver
     * @param path of the Page
     * @return true if the input Page path as one or more children, false instead */
    public static boolean pageHasChildrenPages(final ResourceResolver resourceResolver, final String path) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource == null) {
            return false;
        }
        final List<Resource> resources = ResourceUtilsNeo.getResourcesRecursively(resource);
        return resources.stream().anyMatch(PageOperationsUtils::isResourceTypePage);
    }

    /** @param resourceResolver the ResourceResolver
     * @param path of the Page
     * @param name of Resource to find
     * @return true if the input Page path as one or more children, false instead */
    public static boolean pageHasResourceWithName(ResourceResolver resourceResolver, String path, String name) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Cannot create a Resource object with path %s", path));
        }
        final List<Resource> resources = ResourceUtilsNeo.getResourcesRecursively(resource);
        return resources.stream().map(Resource::getName).anyMatch(resName -> resName.equals(name));
    }

}
