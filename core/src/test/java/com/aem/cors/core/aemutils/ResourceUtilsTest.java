package com.aem.cors.core.aemutils;

import com.aem.cors.core.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.aem.cors.core.aemutils.ResourceUtils.getParentResourceByResourceType;
import static com.aem.cors.core.aemutils.ResourceUtils.getResourceFromPath;
import static com.aem.cors.core.aemutils.ResourceUtils.getResourcesRecursively;
import static com.aem.cors.core.aemutils.ResourceUtils.isNonExistingResourceType;
import static com.aem.cors.core.aemutils.ResourceUtils.updateProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(AemContextExtension.class)
class ResourceUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceUtilsTest.class);

    private final AemContext context = AppAemContext.newAemContext();

    private Resource parent;
    private Resource child;

    @BeforeEach
    void setup() {
        parent = context.create().resource("/content/foo", "sling:resourceType", "my/parent/type");
        child = context.create().resource("/content/foo/child", "sling:resourceType", "my/child/type");
    }

    @Test
    void testGetResourceFromPathExisting() {
        assertThat(getResourceFromPath(context.resourceResolver(), "/content/foo").getPath(), is(parent.getPath()));
    }

    @Test
    void testGetResourceFromPathMissing() {
        assertThat(getResourceFromPath(context.resourceResolver(), "/content/missing"), nullValue());
    }

    @Test
    void testGetResourcesRecursivelyNull() {
        assertThat(getResourcesRecursively(null), is(List.of()));
    }

    @Test
    void testGetResourcesRecursivelyIncludesChildren() {
        List<String> paths = getResourcesRecursively(parent).stream().map(Resource::getPath).collect(Collectors.toList());
        assertThat(paths, is(List.of(parent.getPath(), child.getPath())));
    }

    @Test
    void testIsNonExistingResourceTypeTrue() {
        assertThat(isNonExistingResourceType("sling:nonexisting"), is(true));
    }

    @Test
    void testIsNonExistingResourceTypeFalse() {
        assertThat(isNonExistingResourceType("my/type"), is(false));
    }

    @Test
    void testGetParentResourceByResourceTypeNullChild() {
        assertThat(getParentResourceByResourceType(null, "my/parent/type"), nullValue());
    }

    @Test
    void testGetParentResourceByResourceTypeFound() {
        assertThat(getParentResourceByResourceType(child, "my/parent/type").getPath(), is(parent.getPath()));
    }

    @Test
    void testGetParentResourceByResourceTypeNotFound() {
        assertThat(getParentResourceByResourceType(child, "unknown/type"), nullValue());
    }

    @Test
    void testUpdateProperty() {
        ResourceResolver resourceResolver = context.resourceResolver();
        ModifiableValueMap modifiableValueMap = child.adaptTo(ModifiableValueMap.class);

        updateProperty(resourceResolver, modifiableValueMap, "title", "New Title", LOG, "tr-1");

        assertThat(context.resourceResolver().getResource("/content/foo/child").getValueMap().get("title", String.class),
                notNullValue());
    }
}
