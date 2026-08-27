package com.aem.cors.core.aemutils;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.aem.cors.core.aemutils.ResourceSortUtils.sort;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResourceSortUtilsTest {

    @Mock
    Resource childResource;
    @Mock
    Resource jcrContentResource;

    @Test
    void testSortPutsJcrContentFirst() {
        when(childResource.getPath()).thenReturn("/content/foo/child");
        when(jcrContentResource.getPath()).thenReturn("/content/foo/jcr:content");

        List<Resource> input = new ArrayList<>(List.of(childResource, jcrContentResource));
        Iterator<Resource> result = sort(input.iterator());

        List<Resource> sorted = new ArrayList<>();
        result.forEachRemaining(sorted::add);

        assertThat(sorted.get(0), is(jcrContentResource));
        assertThat(sorted.get(1), is(childResource));
    }

    @Test
    void testSortEmptyIterator() {
        Iterator<Resource> result = sort(List.<Resource>of().iterator());
        assertThat(result.hasNext(), is(false));
    }

    @Test
    void testJcrContentFirstComparatorNeitherIsJcrContent() {
        ResourceSortUtils.JcrContentFirst comparator = new ResourceSortUtils.JcrContentFirst();
        when(childResource.getPath()).thenReturn("/content/foo/a");
        when(jcrContentResource.getPath()).thenReturn("/content/foo/b");
        assertThat(comparator.compare(childResource, jcrContentResource), is(0));
    }
}
