package com.aem.cors.core.aemutils;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Iterator;
import java.util.List;

import static com.aem.cors.core.aemutils.TagsUtils.getTagIdsFromTitles;
import static com.aem.cors.core.aemutils.TagsUtils.getTagName;
import static com.aem.cors.core.aemutils.TagsUtils.getTagTitle;
import static com.aem.cors.core.aemutils.TagsUtils.getTagsValue;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TagsUtilsTest {

    @Mock
    Resource resource;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    TagManager tagManager;
    @Mock
    Tag tag;
    @Mock
    Tag rootTag;

    @Test
    void testGetTagsValueNullTags() {
        assertThat(getTagsValue(resource, null), is(List.of()));
    }

    @Test
    void testGetTagsValueEmptyTags() {
        assertThat(getTagsValue(resource, new String[0]), is(List.of()));
    }

    @Test
    void testGetTagsValueResolvesTitles() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(tagManager.resolve("my-ns:tag1")).thenReturn(tag);
        when(tag.getTitle(java.util.Locale.getDefault())).thenReturn("Tag One");

        assertThat(getTagsValue(resource, new String[] {"my-ns:tag1"}), is(List.of("Tag One")));
    }

    @Test
    void testGetTagsValueNoTagManager() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(null);

        assertThat(getTagsValue(resource, new String[] {"my-ns:tag1"}), is(List.of()));
    }

    @Test
    void testGetTagNameBlankInput() {
        assertThat(getTagName(resource, null), is(EMPTY));
    }

    @Test
    void testGetTagNameResolved() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(tagManager.resolve("my-ns:tag1")).thenReturn(tag);
        when(tag.getName()).thenReturn("tag1");

        assertThat(getTagName(resource, "my-ns:tag1"), is("tag1"));
    }

    @Test
    void testGetTagNameUnresolvedTag() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(tagManager.resolve("my-ns:missing")).thenReturn(null);

        assertThat(getTagName(resource, "my-ns:missing"), is(EMPTY));
    }

    @Test
    void testGetTagTitleResolved() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(tagManager.resolve("my-ns:tag1")).thenReturn(tag);
        when(tag.getTitle()).thenReturn("Tag One");

        assertThat(getTagTitle(resource, "my-ns:tag1"), is("Tag One"));
    }

    @Test
    void testGetTagIdsFromTitlesMatchesByTitle() {
        when(tagManager.resolve("my-ns")).thenReturn(rootTag);
        Iterator<Tag> iterator = List.of(tag).iterator();
        when(rootTag.listChildren()).thenReturn(iterator);
        when(tag.getTitle()).thenReturn("Match");
        when(tag.getTagID()).thenReturn("my-ns:match");

        assertThat(getTagIdsFromTitles(tagManager, List.of("Match"), "my-ns"), is(List.of("my-ns:match")));
    }

    @Test
    void testGetTagIdsFromTitlesNoRootTag() {
        when(tagManager.resolve("missing-ns")).thenReturn(null);
        assertThat(getTagIdsFromTitles(tagManager, List.of("Match"), "missing-ns"), is(List.of()));
    }
}
