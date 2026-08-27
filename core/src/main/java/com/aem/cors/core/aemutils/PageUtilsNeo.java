package com.aem.cors.core.aemutils;

import static com.day.cq.wcm.api.NameConstants.PN_SLING_VANITY_PATH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.personalization.Location;
import com.day.cq.personalization.TargetedContentManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.crx.JcrConstants;
import com.aem.cors.core.utils.StreamUtils;

public final class PageUtilsNeo {

    public static final String PAGE_WITH_LANGUAGE_REGEX = "(.*/)(de|en|fr|it)(/.*)";
    public static final String REGEX_GROUP_1 = "$1";
    public static final String REGEX_GROUP_3 = "$3";
    public static final Predicate<Resource> IS_PAGE = (Resource r) -> r.getValueMap()
            .get(JcrConstants.JCR_PRIMARYTYPE)
            .equals(NameConstants.NT_PAGE);
    private static final Logger LOGGER = LoggerFactory.getLogger(PageUtilsNeo.class);
    private static final String LOCATION = "location";
    private static final String RT_TEASER_PAGE = "cq/personalization/components/teaserpage";

    private PageUtilsNeo() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static String getPageNavigationTitle(final Page page) {
        if (page == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultIfBlank(page.getNavigationTitle(), page.getTitle());
    }

    /** Given a page it returns its vanity URls
     *
     * @param page page
     * @return List that contains the vanity URLs */
    public static List<String> getVanityUrls(Page page) {
        if (page == null || page.getProperties() == null) {
            return Collections.emptyList();
        }
        ValueMap properties = page.getProperties();
        String[] vanityUrlList = PropertiesUtil.toStringArray(properties.get(PN_SLING_VANITY_PATH, ArrayUtils.EMPTY_STRING_ARRAY));
        return Arrays.asList(vanityUrlList);
    }

    /** Having the resource resolve and a resource, return the page where the resource is contained
     *
     * @param resource a resource
     * @return Page corresponding to input resource */
    public static Page getPageFromResource(final Resource resource) {
        if (resource == null) {
            return null;
        }
        final ResourceResolver resourceResolver = resource.getResourceResolver();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return null;
        }
        return pageManager.getContainingPage(resource);
    }

    /** Return a parent page of the current page.
     *
     * The page at `parentLevel`, e.g: level 0 is the site root page
     *
     * @param currentPage currentPage
     * @param parentLevel parentLevel
     * @return a Page object */
    public static Page getParentPageAtSpecificLevel(final Page currentPage, final int parentLevel) {
        if (currentPage == null) {
            return null;
        }
        // We have to subtract 1 from the page level because 'Page.getDepth()' will return a 'non-zero based' integer
        // while the values of our constants are meant to be zero based.
        final int pageDepth = currentPage.getDepth() - 1;
        if (pageDepth == parentLevel) {
            return currentPage;
        }
        if (pageDepth < parentLevel) {
            return null;
        }
        return currentPage.getAbsoluteParent(parentLevel);
    }

    /** Given an input resource as parent
     *
     * Fetch all the children recursively and test if the resource is also a Page
     *
     * all Page(s) are returned to the List
     *
     * @param resource a Resource to start from
     * @return a List of Resource with jcr:primaryType === cq:Page */
    public static List<Resource> getPagesRecursively(final Resource resource) {
        if (resource == null) {
            return Collections.emptyList();
        }

        final List<Resource> resources = new ArrayList<>();

        // Add the given resource itself to the result list only if is page
        if (IS_PAGE.test(resource)) {
            resources.add(resource);
        } else {
            return Collections.emptyList();
        }

        // Recursively fetch all resources of the given children:
        for (final Resource child : resource.getChildren()) {
            if (IS_PAGE.test(child)) {
                resources.addAll(getPagesRecursively(child));
            }
        }

        return Collections.unmodifiableList(resources);
    }

    /** Given an input page as parent
     *
     * Fetch all the descendants pages recursively applying the given pageFilter
     *
     * @param page a Page to start from
     * @param pageFilter implementation of PageFilter that is used to filter all the descendant pages. May be null.
     * @param deep true if the listChildren should return children of children, false only siblings
     * @return a List of Page */
    public static List<Page> getAllDescendantPagesFiltered(final Page page, final PageFilter pageFilter, final boolean deep) {
        if (page == null) {
            return Collections.emptyList();
        }
        final Iterator<Page> children = page.listChildren(pageFilter, deep);
        return StreamUtils.toStream(children).collect(Collectors.toList());
    }

    /** Given an input resource as parent
     *
     * Fetch all the descendants pages recursively applying the given pageFilter
     *
     * @param page a Page to start from
     * @param pageFilter implementation of PageFilter that is used to filter all the descendant pages. May be null.
     * @return a List of Page */
    public static List<Page> getAllDescendantPagesFiltered(final Page page, final PageFilter pageFilter) {
        return getAllDescendantPagesFiltered(page, pageFilter, true);
    }

    /** Given a parent page
     *
     * Returns all the 1st level subPages in a List
     *
     * @param page the parent page
     * @return a List of Page(s) or an empty list */
    public static List<Page> getPageChildren(final Page page) {
        if (page == null) {
            return Collections.emptyList();
        }
        List<Page> result = new ArrayList<>();
        final Resource resource = page.adaptTo(Resource.class);
        if (resource != null && resource.hasChildren()) {
            for (final Resource child : resource.getChildren()) {
                if (IS_PAGE.test(child)) {
                    result.add(child.adaptTo(Page.class));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Find a parent Page of the given Resource
     *
     * @param resource Resource
     * @return parent resource */
    public static Optional<Page> getParentPage(final Resource resource) {
        final Resource parent = resource.getParent();
        if (parent != null && IS_PAGE.test(parent)) {
            return Optional.of(parent.adaptTo(Page.class));
        }
        return Optional.empty();
    }

    /** Find a parent Page of the given child resource by a specific resourcetype
     *
     * @param child Resource
     * @param parentResourceType resource type
     * @return parent resource */
    public static Page getParentPageByResourceType(final Resource child, final String parentResourceType) {
        final Resource parent = child.getParent();
        if (null == parent) {
            return null;
        }
        if (IS_PAGE.test(parent) && ResourceUtilsNeo.isResourceOfType(parent, parentResourceType)) {
            return parent.adaptTo(Page.class);
        }
        return getParentPageByResourceType(parent, parentResourceType);
    }

    /** @param resourceResolver the ResourceResolver
     * @param path a path
     * @return true if the input path is cq:Page, false instead */
    public static boolean isResourceOfTypePage(final ResourceResolver resourceResolver, final String path) {
        final Resource resource = resourceResolver.getResource(path);
        return IS_PAGE.test(resource);
    }

    /** Check the given page to be of given type
     *
     * @param page the Page
     * @param resType the resource type
     * @return a boolean */
    public static boolean isPageOfType(final Page page, final String resType) {
        if (page == null) {
            return false;
        }
        final Resource contentResource = page.getContentResource();
        return (contentResource != null) && contentResource.isResourceType(resType);
    }

    /** Given the input Page returns the 'sling:resourceType' of the template
     *
     * The 'sling:resourceType' is read from the jcr:content node so it also works for anonymous users that
     * may not have access to the templates under /conf
     *
     * @param page the Page
     * @return a String or null */
    public static String getPageTemplateResourceType(final Page page) {
        if (page != null && page.getContentResource() != null) {
            return page.getContentResource().getResourceType();
        }
        return null;
    }

    /** Given the input resource, retrieve the containing page and delete it
     *
     * @param resourceResolver the ResourceResolver
     * @param resource a Resource to delete
     * @throws WCMException if something goes wrong */
    public static void deletePageFromResource(final ResourceResolver resourceResolver, final Resource resource) throws WCMException {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            final Page page = pageManager.getContainingPage(resource);
            if (page != null) {
                boolean shallow = false; // delete page itself
                boolean autoSave = true;
                pageManager.delete(page, shallow, autoSave);
            }
        }
    }

    /** Given a Page, get the jcr:content node and return a value of type T using the input propertyName
     *
     * If something is null or the property doesn't exist the Object defaultValue is returned
     *
     * @param page a Page
     * @param propertyName the property name
     * @param defaultValue a default value as type T
     * @return a value of type T */
    public static <T> T getPageProperty(final Page page, final String propertyName, final T defaultValue) {
        if (page != null) {
            final Resource jcrContent = page.getContentResource();
            if (jcrContent != null) {
                final ValueMap valueMap = jcrContent.getValueMap();
                if (valueMap.containsKey(propertyName)) {
                    return valueMap.get(propertyName, defaultValue);
                }
            }
        }
        return defaultValue;
    }

    public static boolean isTargetedPage(Page page) {
        return Optional.ofNullable(page)
                .map(Page::getContentResource)
                .map(Resource::getResourceType)
                .filter(RT_TEASER_PAGE::equals)
                .isPresent();
    }

    public static Optional<Page> getOriginalPageFromTargetedPage(Page targetedPage, ResourceResolver resourceResolver,
            TargetedContentManager targetedContentManager) {
        return getLocationIdFromTargetedPage(targetedPage)
                .flatMap(location -> targetedContentManager.findLocations(resourceResolver, location)
                        .stream()
                        .findFirst()
                        .map(Location::getPagePath)
                        .map(resourceResolver::getResource)
                        .map(r -> r.adaptTo(Page.class)));
    }

    /** Given a master page returns the same page for a different language
     *
     * @param masterPage the master page (normally DE language)
     * @param languageCode the desired language
     * @return an Optional with the page for the given language or empty it does not exist */
    public static Optional<Page> getPageForLanguage(Page masterPage, String languageCode) {
        if (masterPage == null) {
            return Optional.empty();
        }
        final var pathOfMasterPage = masterPage.getPath();
        LOGGER.debug("Path of the master page is: {}", pathOfMasterPage);
        final var pathForLanguage = Pattern.compile(PAGE_WITH_LANGUAGE_REGEX)
                .matcher(pathOfMasterPage)
                .replaceAll(REGEX_GROUP_1 + languageCode + REGEX_GROUP_3);
        LOGGER.debug("Path of the page for language {} is: {}", languageCode, pathForLanguage);
        final var languagePage = masterPage.getPageManager().getPage(pathForLanguage);
        return Optional.ofNullable(languagePage);
    }

    private static Optional<String> getLocationIdFromTargetedPage(Page targetedPage) {
        return Optional.ofNullable(targetedPage)
                .map(Page::getContentResource)
                .map(Resource::getValueMap)
                .map(vm -> vm.get(LOCATION, String.class));
    }

    /** Given a Page path, get the parsys
     *
     * If the parsys is empty, the page is considered empty
     *
     * If the parsys is not empty, the page is not considered empty
     *
     * @param resourceResolver the ResourceResolver
     * @param path of the Page
     * @param parsysRelativePath the relative path (from jcr:content) of the parsys to check
     * @return true or false */
    public static Boolean isPageEmpty(final ResourceResolver resourceResolver, final String path, final String parsysRelativePath) {
        if (path == null) {
            return Boolean.FALSE;
        }
        final Resource resourceAtPath = resourceResolver.getResource(path);
        if (resourceAtPath == null || !IS_PAGE.test(resourceAtPath)) {
            return Boolean.FALSE;
        }
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            final Page page = pageManager.getPage(path);
            if (page != null) {
                final Resource pageJcrContent = page.getContentResource();
                if (pageJcrContent != null) {
                    final Resource pageParsys = pageJcrContent.getChild(parsysRelativePath);
                    if (pageParsys != null) {
                        return !pageParsys.hasChildren();
                    }
                }
            }
        }
        return false;
    }
}
