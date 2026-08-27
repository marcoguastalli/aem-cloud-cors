package com.aem.cors.core.aemutils;

import com.aem.cors.core.AppAemContext;
import com.aem.cors.core.exceptions.AemRuntimeException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aem.cors.core.aemutils.ResourceUtilsNeo.buildResourceForDataSource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.createResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.deleteResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getChildFromPath;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getChildrenWithResourceType;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getModifiableValueMapFromPath;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getNumberOfChildren;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getObjectPropertyFromResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getParentResourceByResourceType;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getResourcesRecursively;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getStringPropertyFromResource;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.getValueMapFromPath;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.isResourceOfPrimaryType;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.isResourceOfType;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.mergeResourceProperties;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.retrievePersonalisedComponentResourceType;
import static com.aem.cors.core.aemutils.ResourceUtilsNeo.updatePropertyValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(AemContextExtension.class)
class ResourceUtilsNeoTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Resource parent;
    private Resource child;

    @BeforeEach
    void setup() {
        parent = context.create().resource("/content/foo", "sling:resourceType", "my/parent/type", "jcr:primaryType", "nt:unstructured");
        child = context.create().resource("/content/foo/child", "sling:resourceType", "my/child/type", "jcr:primaryType", "nt:unstructured");
    }

    @Test
    void testCreateResourceNew() throws Exception {
        // 'newResourceName' is a simple node name, not a path - matches how production code calls
        // this (e.g. ResourceMultiValueFieldUtils passes "item" + i)
        Map<String, Object> properties = new HashMap<>();
        properties.put("sling:resourceType", "my/new/type");

        Resource result = createResource(context.resourceResolver(), parent, "newChild", properties);

        assertThat(result, notNullValue());
        assertThat(context.resourceResolver().getResource("/content/foo/newChild"), notNullValue());
    }

    @Test
    void testDeleteResourceByPath() throws Exception {
        deleteResource(context.resourceResolver(), "/content/foo/child");
        assertThat(context.resourceResolver().getResource("/content/foo/child"), nullValue());
    }

    @Test
    void testMergeResourcePropertiesOnExistingResource() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("title", "New Title");

        mergeResourceProperties(context.resourceResolver(), "/content/foo/child", properties);

        assertThat(context.resourceResolver().getResource("/content/foo/child").getValueMap().get("title", String.class),
                is("New Title"));
    }

    @Test
    void testMergeResourcePropertiesCreatesMissingResource() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("sling:resourceType", "my/new/type");

        mergeResourceProperties(context.resourceResolver(), "/content/foo/newChild", properties);

        assertThat(context.resourceResolver().getResource("/content/foo/newChild"), notNullValue());
    }

    @Test
    void testMergeResourcePropertiesThrowsWhenNoParent() {
        Map<String, Object> properties = new HashMap<>();
        assertThrows(AemRuntimeException.class,
                () -> mergeResourceProperties(context.resourceResolver(), "/content/missing/deep/path", properties));
    }

    @Test
    void testGetModifiableValueMapFromPath() {
        ModifiableValueMap result = getModifiableValueMapFromPath(context.resourceResolver(), "/content/foo/child");
        assertThat(result.get("sling:resourceType", String.class), is("my/child/type"));
    }

    @Test
    void testGetModifiableValueMapFromPathMissingThrows() {
        assertThrows(AemRuntimeException.class, () -> getModifiableValueMapFromPath(context.resourceResolver(), "/content/missing"));
    }

    @Test
    void testGetValueMapFromPath() {
        ValueMap result = getValueMapFromPath(context.resourceResolver(), "/content/foo/child");
        assertThat(result.get("sling:resourceType", String.class), is("my/child/type"));
    }

    @Test
    void testGetValueMapFromPathMissingThrows() {
        assertThrows(AemRuntimeException.class, () -> getValueMapFromPath(context.resourceResolver(), "/content/missing"));
    }

    @Test
    void testDeleteResourceInstance() throws Exception {
        deleteResource(context.resourceResolver(), child);
        assertThat(context.resourceResolver().getResource("/content/foo/child"), nullValue());
    }

    @Test
    void testGetChildrenWithResourceType() {
        Collection<Resource> result = getChildrenWithResourceType(parent, "my/child/type");
        assertThat(result.size(), is(1));
        assertThat(result.iterator().next().getPath(), is(child.getPath()));
    }

    @Test
    void testGetObjectPropertyFromResource() {
        assertThat(getObjectPropertyFromResource(child, "sling:resourceType"), is("my/child/type"));
    }

    @Test
    void testGetObjectPropertyFromResourceMissingProperty() {
        assertThat(getObjectPropertyFromResource(child, "missing"), nullValue());
    }

    @Test
    void testGetObjectPropertyFromResourceNullResourceThrows() {
        assertThrows(AemRuntimeException.class, () -> getObjectPropertyFromResource(null, "any"));
    }

    @Test
    void testGetStringPropertyFromResource() {
        assertThat(getStringPropertyFromResource(context.resourceResolver(), "/content/foo/child", "sling:resourceType", "default"),
                is("my/child/type"));
    }

    @Test
    void testGetStringPropertyFromResourceMissingPathReturnsDefault() {
        assertThat(getStringPropertyFromResource(context.resourceResolver(), "/content/missing", "sling:resourceType", "default"),
                is("default"));
    }

    @Test
    void testGetNumberOfChildrenNoChildren() {
        assertThat(getNumberOfChildren(child), is(0L));
    }

    @Test
    void testGetNumberOfChildrenWithChildren() {
        assertThat(getNumberOfChildren(parent), is(1L));
    }

    @Test
    void testUpdatePropertyValue() throws Exception {
        updatePropertyValue(context.resourceResolver(), child, "title", "Updated");
        assertThat(context.resourceResolver().getResource("/content/foo/child").getValueMap().get("title", String.class), is("Updated"));
    }

    @Test
    void testGetParentResourceByResourceTypeFound() {
        Resource result = getParentResourceByResourceType(child, "my/parent/type");
        assertThat(result.getPath(), is(parent.getPath()));
    }

    @Test
    void testGetParentResourceByResourceTypeNullChild() {
        assertThat(getParentResourceByResourceType(null, "my/parent/type"), nullValue());
    }

    @Test
    void testIsResourceOfTypeTrue() {
        assertThat(isResourceOfType(child, "my/child/type"), is(true));
    }

    @Test
    void testIsResourceOfTypeNullResource() {
        assertThat(isResourceOfType(null, "my/child/type"), is(false));
    }

    @Test
    void testIsResourceOfPrimaryType() {
        assertThat(isResourceOfPrimaryType(parent, "nt:unstructured"), is(true));
    }

    @Test
    void testGetResourcesRecursivelyNull() {
        assertThat(getResourcesRecursively(null), is(List.of()));
    }

    @Test
    void testGetResourcesRecursively() {
        List<String> paths = getResourcesRecursively(parent).stream().map(Resource::getPath).collect(Collectors.toList());
        assertThat(paths, is(List.of(parent.getPath(), child.getPath())));
    }

    @Test
    void testRetrievePersonalisedComponentResourceType() {
        context.create().resource("/content/foo/default", "sling:resourceType", "my/default/type");
        assertThat(retrievePersonalisedComponentResourceType(context.resourceResolver(), "/content/foo"), is("my/default/type"));
    }

    @Test
    void testGetChildFromPath() {
        Resource result = getChildFromPath(parent, "/child");
        assertThat(result.getPath(), is(child.getPath()));
    }

    @Test
    void testGetChildFromPathNullParent() {
        assertThat(getChildFromPath(null, "/child"), nullValue());
    }

    @Test
    void testBuildResourceForDataSource() {
        ResourceResolver resourceResolver = context.resourceResolver();
        Resource result = buildResourceForDataSource("Text", "value1", resourceResolver);

        assertThat(result.getValueMap().get("text", String.class), is("Text"));
        assertThat(result.getValueMap().get("value", String.class), is("value1"));
    }

    @Test
    void testBuildResourceForDataSourceDefaultsTextToValue() {
        Resource result = buildResourceForDataSource(null, "value1", context.resourceResolver());
        assertThat(result.getValueMap().get("text", String.class), is("value1"));
    }
}
