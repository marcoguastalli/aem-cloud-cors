package com.aem.cors.core.aemutils;

import com.aem.cors.core.AppAemContext;
import com.aem.cors.core.exceptions.AemRuntimeException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.addValueToMultiValueField;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.checkValueInMultiValueField;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.getMultiValueFieldPropertyValues;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.getMultiValuePropertyValueAsString;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.mergeResourceMultiValueFieldProperty;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.removeValueFromMultiValueField;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.replaceResourceMultiValueFieldProperty;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.resetMultiValueFieldProperty;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.saveOrReplaceMultiValueParameterInResource;
import static com.aem.cors.core.aemutils.ResourceMultiValueFieldUtils.setMultiValueFieldChildren;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(AemContextExtension.class)
class ResourceMultiValueFieldUtilsTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Resource resource;

    @BeforeEach
    void setup() {
        resource = context.create().resource("/content/foo", "jcr:primaryType", "nt:unstructured");
    }

    @Test
    void testGetMultiValuePropertyValueAsStringArray() {
        context.create().resource("/content/withvalues", "tags", new String[] {"a", "b"});
        String[] result = ResourceMultiValueFieldUtils.getMultiValuePropertyValueAsString(context.resourceResolver(),
                "/content/withvalues", "tags", new String[0]);
        assertThat(result, is(new String[] {"a", "b"}));
    }

    @Test
    void testGetMultiValuePropertyValueAsStringMissingResourceReturnsDefault() {
        String[] result = ResourceMultiValueFieldUtils.getMultiValuePropertyValueAsString(context.resourceResolver(),
                "/content/missing", "tags", new String[] {"default"});
        assertThat(result, is(new String[] {"default"}));
    }

    @Test
    void testGetMultiValuePropertyValueAsStringJoined() {
        context.create().resource("/content/withvalues", "tags", new String[] {"a", "b"});
        String result = getMultiValuePropertyValueAsString(context.resourceResolver(), "/content/withvalues", "tags", ",", "default");
        assertThat(result, is("a,b"));
    }

    @Test
    void testGetMultiValuePropertyValueAsStringJoinedDefault() {
        String result = getMultiValuePropertyValueAsString(context.resourceResolver(), "/content/missing", "tags", ",", "default");
        assertThat(result, is("default"));
    }

    @Test
    void testAddValueToMultiValueFieldCreatesArray() {
        addValueToMultiValueField(context.resourceResolver(), "/content/foo", "tags", "first");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"first"}));
    }

    @Test
    void testAddValueToMultiValueFieldAppendsToExisting() {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"first"});
        addValueToMultiValueField(context.resourceResolver(), "/content/foo", "tags", "second");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"first", "second"}));
    }

    @Test
    void testRemoveValueFromMultiValueFieldRemovesPropertyWhenLast() {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"only"});
        removeValueFromMultiValueField(context.resourceResolver(), "/content/foo", "tags", "only");
        assertThat(context.resourceResolver().getResource("/content/foo").getValueMap().get("tags"), nullValue());
    }

    @Test
    void testRemoveValueFromMultiValueFieldKeepsRemaining() {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"a", "b"});
        removeValueFromMultiValueField(context.resourceResolver(), "/content/foo", "tags", "a");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"b"}));
    }

    @Test
    void testCheckValueInMultiValueFieldAddsIfMissing() {
        checkValueInMultiValueField(context.resourceResolver(), "/content/foo", "tags", "new");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"new"}));
    }

    @Test
    void testCheckValueInMultiValueFieldNoDuplicates() {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"existing"});
        checkValueInMultiValueField(context.resourceResolver(), "/content/foo", "tags", "existing");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"existing"}));
    }

    @Test
    void testResetMultiValueFieldProperty() throws Exception {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"a", "b"});
        resetMultiValueFieldProperty(context.resourceResolver(), "/content/foo", "tags");
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[0]));
    }

    @Test
    void testMergeResourceMultiValueFieldPropertyOnEmpty() throws Exception {
        mergeResourceMultiValueFieldProperty(context.resourceResolver(), "/content/foo", "tags", List.of("a", "b"));
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(List.of(result), is(List.of("a", "b")));
    }

    @Test
    void testMergeResourceMultiValueFieldPropertyDeduplicates() throws Exception {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"a"});
        mergeResourceMultiValueFieldProperty(context.resourceResolver(), "/content/foo", "tags", List.of("a", "b"));
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(List.of(result), is(List.of("a", "b")));
    }

    @Test
    void testGetMultiValueFieldPropertyValuesMissingResourceThrows() {
        assertThrows(AemRuntimeException.class,
                () -> getMultiValueFieldPropertyValues(context.resourceResolver(), "/content/missing", "reference"));
    }

    @Test
    void testGetMultiValueFieldPropertyValuesNoChildren() {
        List<String> result = getMultiValueFieldPropertyValues(context.resourceResolver(), "/content/foo", "reference");
        assertThat(result, is(List.of()));
    }

    @Test
    void testGetMultiValueFieldPropertyValuesNestedStructure() {
        Resource field = context.create().resource("/content/foo/field", "jcr:primaryType", "nt:unstructured");
        context.create().resource("/content/foo/field/item0", "reference", "/content/ref1");
        context.create().resource("/content/foo/field/item1", "reference", "/content/ref2");

        List<String> result = getMultiValueFieldPropertyValues(context.resourceResolver(), "/content/foo", "reference");

        assertThat(result, is(List.of("/content/ref1", "/content/ref2")));
    }

    @Test
    void testReplaceResourceMultiValueFieldPropertyChangesValue() throws Exception {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"old"});

        boolean replaced = replaceResourceMultiValueFieldProperty(context.resourceResolver(), "/content/foo", "tags", List.of("new"));

        assertThat(replaced, is(true));
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"new"}));
    }

    @Test
    void testReplaceResourceMultiValueFieldPropertyNoChangeWhenSame() throws Exception {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"same"});
        // commit the setup change so hasChanges() below reflects only what replaceResourceMultiValueFieldProperty itself does
        context.resourceResolver().commit();

        boolean replaced = replaceResourceMultiValueFieldProperty(context.resourceResolver(), "/content/foo", "tags", List.of("same"));

        assertThat(replaced, is(false));
    }

    @Test
    void testSaveOrReplaceMultiValueParameterInResourceAddsNewValue() throws Exception {
        saveOrReplaceMultiValueParameterInResource(context.resourceResolver(), resource, "tags", new String[] {"a", "b"});
        String[] result = (String[]) context.resourceResolver().getResource("/content/foo").getValueMap().get("tags");
        assertThat(result, is(new String[] {"a", "b"}));
    }

    @Test
    void testSaveOrReplaceMultiValueParameterInResourceRemovesWhenEmpty() throws Exception {
        context.resourceResolver().getResource("/content/foo").adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("tags", new String[] {"a"});

        saveOrReplaceMultiValueParameterInResource(context.resourceResolver(), resource, "tags", new String[0]);

        assertThat(context.resourceResolver().getResource("/content/foo").getValueMap().get("tags"), nullValue());
    }

    @Test
    void testSetMultiValueFieldChildrenMissingResourceThrows() {
        assertThrows(AemRuntimeException.class,
                () -> setMultiValueFieldChildren(context.resourceResolver(), "/content/missing", "reference", List.of("a")));
    }

    @Test
    void testSetMultiValueFieldChildrenReplacesItems() {
        context.create().resource("/content/foo/field", "jcr:primaryType", "nt:unstructured");
        context.create().resource("/content/foo/field/item0", "reference", "/content/old");

        setMultiValueFieldChildren(context.resourceResolver(), "/content/foo", "reference", List.of("/content/new1", "/content/new2"));

        List<String> result = getMultiValueFieldPropertyValues(context.resourceResolver(), "/content/foo", "reference");
        assertThat(result, is(List.of("/content/new1", "/content/new2")));
    }

    @Test
    void testSetMultiValueFieldChildrenEmptyRemovesComponent() {
        context.create().resource("/content/foo/field", "jcr:primaryType", "nt:unstructured");
        context.create().resource("/content/foo/field/item0", "reference", "/content/old");

        setMultiValueFieldChildren(context.resourceResolver(), "/content/foo", "reference", List.of());

        assertThat(context.resourceResolver().getResource("/content/foo"), nullValue());
    }
}
