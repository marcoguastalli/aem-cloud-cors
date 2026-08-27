package com.aem.cors.core.utils.page.domain;

import java.io.Serializable;
import java.util.Objects;

/** Bean class containing cq:Page information */
public class PageInfo implements Serializable {

    private final String path;
    private final String resourceType;
    private final String parentPagePath;
    private final String parentLanguagePagePath;

    public PageInfo(final String path, final String resourceType, final String parentPagePath, final String parentLanguagePagePath) {
        this.path = path;
        this.resourceType = resourceType;
        this.parentPagePath = parentPagePath;
        this.parentLanguagePagePath = parentLanguagePagePath;
    }

    public String getPath() {
        return path;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getParentPagePath() {
        return parentPagePath;
    }

    public String getParentLanguagePagePath() {
        return parentLanguagePagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageInfo pageInfo = (PageInfo) o;
        return path.equals(pageInfo.path) && resourceType.equals(pageInfo.resourceType) && parentPagePath.equals(pageInfo.parentPagePath)
                && parentLanguagePagePath.equals(pageInfo.parentLanguagePagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, resourceType, parentPagePath, parentLanguagePagePath);
    }
}
