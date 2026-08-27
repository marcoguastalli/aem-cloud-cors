package com.aem.cors.core.aemutils;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

/** Utility class for sorting Resources */
public final class ResourceSortUtils {

    private ResourceSortUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static Iterator<Resource> sort(final Iterator<Resource> resources) {
        List<Resource> result = new ArrayList<>();
        // add input Resource to a new List
        resources.forEachRemaining(result::add);
        // sort with JCR_CONTENT in first position
        result.sort(new JcrContentFirst());
        return result.iterator();
    }

    static final class JcrContentFirst implements Comparator<Resource> {
        @Override
        public int compare(Resource r1, Resource r2) {
            boolean r1pathCondition = r1.getPath().endsWith(JCR_CONTENT);
            boolean r2pathCondition = r2.getPath().endsWith(JCR_CONTENT);
            if (!r1pathCondition && r2pathCondition) {
                return 1;
            } else if (r1pathCondition && !r2pathCondition) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
