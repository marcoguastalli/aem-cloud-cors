package com.aem.cors.core.aemutils.page;

import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import com.aem.cors.core.aemutils.ResourceUtilsNeo;

public final class PageManagingUtils {

    private PageManagingUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Copy the input page at the destination path, before the existing page 'beforeName'
     *
     * @param pageManager the PageManager
     * @param destination the destination path
     * @param page to be copied at the destination path
     * @param beforeName the name of the next page. if null the page is ordered at the end
     * @return a Page object instance with the new page
     * @throws WCMException if the copy fails */
    public static Page copyPage(final PageManager pageManager, final String destination, final Page page, final String beforeName)
            throws WCMException {
        boolean shallow = true; // Do not copy subpages.
        boolean resolveConflict = false; // not relevant, already checked that targetPage does not exist
        return pageManager.copy(page, destination, beforeName, shallow, resolveConflict);// WCMException
    }

    /** Create 2 Page object from the input path1 and path2
     *
     * Recursively check that the 2 pages structure are equals
     *
     * @param pageManager the PageManager
     * @param path1 the path of the first source
     * @param path2 the path of the second page
     * @return true if the pages structure are equals, false instead */
    public static boolean equalsPagesStructure(final PageManager pageManager, final String path1, final String path2) {
        final Page page1 = pageManager.getPage(path1);
        final Page page2 = pageManager.getPage(path2);
        if (page1 == null || page2 == null) {
            return false;
        }
        final List<Resource> resourcesPage1 = ResourceUtilsNeo.getResourcesRecursively(page1.getContentResource());
        final List<Resource> resourcesPage2 = ResourceUtilsNeo.getResourcesRecursively(page2.getContentResource());
        if (resourcesPage1.size() != resourcesPage2.size()) {
            return false;
        }

        for (int i = 0; i < resourcesPage1.size(); i++) {
            final Resource resource1 = resourcesPage1.get(i);
            final Resource resource2 = resourcesPage2.get(i);
            if (!resource1.getResourceType().equals(resource2.getResourceType()) ||
                    !resource1.getName().equals(resource2.getName())) {
                return false;
            }
        }

        return true;
    }

}
