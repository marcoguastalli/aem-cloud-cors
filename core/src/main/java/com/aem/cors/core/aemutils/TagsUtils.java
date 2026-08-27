package com.aem.cors.core.aemutils;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/** Util class for AEM Tagging (com.day.cq.tagging) */
public class TagsUtils {

    private TagsUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Resolve the input tag IDs to their localized titles
     *
     * @param resource the Resource used to adapt to a TagManager
     * @param tags the tag IDs to resolve
     * @return a List with the resolved tag titles, or an empty List if tags is null/empty or no TagManager is available */
    public static List<String> getTagsValue(@NotNull Resource resource, @Nullable String[] tags) {
        if (null == tags || ArrayUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
        if (null != tagManager) {
            final Locale locale = Locale.getDefault();
            return Arrays.stream(tags).map(tagManager::resolve).filter(Objects::nonNull).map(tag -> tag.getTitle(locale)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /** Resolve the input tag ID to its short name
     *
     * @param resource the Resource used to adapt to a TagManager
     * @param tagString the tag ID to resolve
     * @return the tag's name, or an empty String if tagString is blank, no TagManager is available, or the tag cannot be resolved */
    public static String getTagName(@NotNull Resource resource, @Nullable String tagString) {
        if (StringUtils.isEmpty(tagString)) {
            return StringUtils.EMPTY;
        }
        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
        if (null == tagManager) {
            return StringUtils.EMPTY;
        }
        Tag tag = tagManager.resolve(tagString);
        if (null == tag) {
            return StringUtils.EMPTY;
        }
        return tag.getName();
    }

    /** Resolve the input tag ID to its localized title
     *
     * @param resource the Resource used to adapt to a TagManager
     * @param tagString the tag ID to resolve
     * @return the tag's title, or an empty String if tagString is blank, no TagManager is available, or the tag cannot be resolved */
    public static String getTagTitle(@NotNull Resource resource, @Nullable String tagString) {
        if (StringUtils.isEmpty(tagString)) {
            return StringUtils.EMPTY;
        }
        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
        if (null == tagManager) {
            return StringUtils.EMPTY;
        }
        Tag tag = tagManager.resolve(tagString);
        if (null == tag) {
            return StringUtils.EMPTY;
        }
        return tag.getTitle();
    }

    /** Given a tag namespace, resolve the IDs of its direct children whose title matches one of the input titles
     *
     * @param tagManager the TagManager
     * @param titles the tag titles to match against the namespace's children
     * @param namespace the root tag ID to search under, e.g.: "my-namespace:my-category"
     * @return a List of matching tag IDs, or an empty List if the namespace cannot be resolved */
    public static List<String> getTagIdsFromTitles(TagManager tagManager, List<String> titles, String namespace) {
        List<String> tagIds = new ArrayList<>();
        // e.g.: "my-namespace:my-category"
        Tag rootTag = tagManager.resolve(namespace);
        if (rootTag == null) {
            return tagIds;
        }

        Iterator<Tag> tagIterator = rootTag.listChildren();
        while (tagIterator.hasNext()) {
            Tag tag = tagIterator.next();
            if (titles.contains(tag.getTitle())) {
                tagIds.add(tag.getTagID());
            }
        }
        return tagIds;
    }

}
