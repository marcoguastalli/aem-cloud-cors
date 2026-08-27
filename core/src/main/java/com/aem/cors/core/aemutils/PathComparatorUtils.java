package com.aem.cors.core.aemutils;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;

import java.util.Comparator;

public class PathComparatorUtils {

    private PathComparatorUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    static final class JcrContentFirst implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            boolean s1Contains = s1.contains(JCR_CONTENT);
            boolean s2Contains = s2.contains(JCR_CONTENT);
            if (!s1Contains && s2Contains) {
                return 1;
            } else if (s1Contains && !s2Contains) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
