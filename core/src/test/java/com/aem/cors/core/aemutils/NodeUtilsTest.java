package com.aem.cors.core.aemutils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.aem.cors.core.aemutils.NodeUtils.getBinaryFromNode;
import static com.aem.cors.core.aemutils.NodeUtils.getBooleanPropertyFromNode;
import static com.aem.cors.core.aemutils.NodeUtils.getDatePropertyFromNode;
import static com.aem.cors.core.aemutils.NodeUtils.getLongPropertyFromNode;
import static com.aem.cors.core.aemutils.NodeUtils.getNodePath;
import static com.aem.cors.core.aemutils.NodeUtils.getOrCreateNode;
import static com.aem.cors.core.aemutils.NodeUtils.getStringPropertyFromNode;
import static com.aem.cors.core.aemutils.NodeUtils.isNonExistingResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NodeUtilsTest {

    @Mock
    Node node;
    @Mock
    Property property;
    @Mock
    Value value;
    @Mock
    Node childNode;

    @Test
    void testGetStringPropertyFromNode() throws RepositoryException {
        when(node.hasProperty("title")).thenReturn(true);
        when(node.getProperty("title")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getString()).thenReturn("Hello");

        assertThat(getStringPropertyFromNode(node, "title"), is("Hello"));
    }

    @Test
    void testGetStringPropertyFromNodeMissingReturnsEmpty() throws RepositoryException {
        when(node.hasProperty("title")).thenReturn(false);

        assertThat(getStringPropertyFromNode(node, "title"), is(""));
    }

    @Test
    void testGetStringPropertyFromNodeWithDefault() throws RepositoryException {
        when(node.hasProperty("title")).thenReturn(false);
        assertThat(getStringPropertyFromNode(node, "title", "default"), is("default"));
    }

    @Test
    void testGetStringPropertyFromNodeWithFallback() throws RepositoryException {
        when(node.hasProperty("title")).thenReturn(false);
        when(node.hasProperty("altTitle")).thenReturn(true);
        when(node.getProperty("altTitle")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getString()).thenReturn("Alt");

        assertThat(getStringPropertyFromNode(node, "title", "altTitle", "default"), is("Alt"));
    }

    @Test
    void testGetDatePropertyFromNode() throws RepositoryException {
        Calendar calendar = new GregorianCalendar(2024, Calendar.JANUARY, 1);
        when(node.hasProperty("date")).thenReturn(true);
        when(node.getProperty("date")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getDate()).thenReturn(calendar);

        assertThat(getDatePropertyFromNode(node, "date"), is(calendar));
    }

    @Test
    void testGetLongPropertyFromNode() throws RepositoryException {
        when(node.hasProperty("count")).thenReturn(true);
        when(node.getProperty("count")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getLong()).thenReturn(42L);

        assertThat(getLongPropertyFromNode(node, "count"), is(42L));
    }

    @Test
    void testGetLongPropertyFromNodeDefaultWhenZero() throws RepositoryException {
        when(node.hasProperty("count")).thenReturn(false);
        assertThat(getLongPropertyFromNode(node, "count", 99L), is(99L));
    }

    @Test
    void testGetBinaryFromNode() throws RepositoryException {
        Binary binary = mock(Binary.class);
        when(node.hasProperty(Property.JCR_DATA)).thenReturn(true);
        when(node.getProperty(Property.JCR_DATA)).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getBinary()).thenReturn(binary);

        assertThat(getBinaryFromNode(node), is(binary));
    }

    @Test
    void testGetBooleanPropertyFromNodeTrue() throws RepositoryException {
        when(node.hasProperty("flag")).thenReturn(true);
        when(node.getProperty("flag")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getBoolean()).thenReturn(true);

        assertThat(getBooleanPropertyFromNode(node, "flag"), is(true));
    }

    @Test
    void testGetBooleanPropertyFromNodeMissingReturnsFalse() throws RepositoryException {
        when(node.hasProperty("flag")).thenReturn(false);
        assertThat(getBooleanPropertyFromNode(node, "flag"), is(false));
    }

    @Test
    void testGetNodePath() throws RepositoryException {
        when(node.getPath()).thenReturn("/content/foo");
        assertThat(getNodePath(node), is("/content/foo"));
    }

    @Test
    void testGetNodePathThrowsReturnsNull() throws RepositoryException {
        when(node.getPath()).thenThrow(new RepositoryException("boom"));
        assertThat(getNodePath(node), nullValue());
    }

    @Test
    void testIsNonExistingResourceNull() {
        assertThat(isNonExistingResource(null), is(true));
    }

    @Test
    void testGetOrCreateNodeExistingWithMatchingType() throws RepositoryException {
        when(node.hasNode("child")).thenReturn(true);
        when(node.getNode("child")).thenReturn(childNode);
        when(childNode.hasProperty("sling:resourceType")).thenReturn(true);
        when(childNode.getProperty("sling:resourceType")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getString()).thenReturn("my/resource/type");

        assertThat(getOrCreateNode(node, "child", "my/resource/type"), is(childNode));
    }

    @Test
    void testGetOrCreateNodeCreatesNewWhenMissing() throws RepositoryException {
        when(node.hasNode("child")).thenReturn(false);
        when(node.addNode("child")).thenReturn(childNode);

        assertThat(getOrCreateNode(node, "child", "my/resource/type"), is(childNode));
        verify(childNode).setProperty("sling:resourceType", "my/resource/type");
    }

    @Test
    void testGetOrCreateNodeMismatchedTypeStillReturnsExistingNode() throws RepositoryException {
        // getOrCreateNode assigns 'result' to the existing child before validating its resourceType,
        // so a mismatch throws IllegalArgumentException which is swallowed by the catch-all logger,
        // and the already-assigned existing node is returned rather than null.
        when(node.hasNode("child")).thenReturn(true);
        when(node.getNode("child")).thenReturn(childNode);
        when(childNode.hasProperty("sling:resourceType")).thenReturn(true);
        when(childNode.getProperty("sling:resourceType")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getString()).thenReturn("other/type");

        assertThat(getOrCreateNode(node, "child", "my/resource/type"), is(childNode));
        verify(childNode, never()).addNode(org.mockito.ArgumentMatchers.anyString());
    }
}
