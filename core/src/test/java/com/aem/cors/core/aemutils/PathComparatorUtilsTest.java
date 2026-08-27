package com.aem.cors.core.aemutils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PathComparatorUtilsTest {

    private final PathComparatorUtils.JcrContentFirst comparator = new PathComparatorUtils.JcrContentFirst();

    @Test
    void testFirstContainsJcrContentSecondDoesNot() {
        assertThat(comparator.compare("/content/foo/jcr:content", "/content/foo"), is(-1));
    }

    @Test
    void testFirstDoesNotContainSecondDoes() {
        assertThat(comparator.compare("/content/foo", "/content/foo/jcr:content"), is(1));
    }

    @Test
    void testBothContainOrNeitherContain() {
        assertThat(comparator.compare("/content/foo/jcr:content", "/content/bar/jcr:content"), is(0));
        assertThat(comparator.compare("/content/foo", "/content/bar"), is(0));
    }
}
