package com.aem.cors.core.aemutils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;

public final class NodeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeUtils.class);

    private NodeUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Returns for the input Node, the corresponding propertyName value as javax.jcr.Value object
     * 
     * @param node the Node
     * @param propertyName the property name
     * @return a javax.jcr.Value object */
    private static Value getPropertyFromNode(final Node node, final String propertyName) {
        try {
            if (node.hasProperty(propertyName)) {
                return node.getProperty(propertyName).getValue();
            }
        } catch (Exception e) {
            LOGGER.error("Error getPropertyFromNode", e);
        }
        return null;
    }

    /** Returns for the input Node, the corresponding propertyName value as String object
     * 
     * @param node the Node
     * @param propertyName the property name
     * @return a String */
    public static String getStringPropertyFromNode(final Node node, final String propertyName) {
        try {
            final Value value = getPropertyFromNode(node, propertyName);
            if (value != null) {
                return value.getString();
            }
        } catch (Exception e) {
            LOGGER.debug("Error getStringPropertyFromNode: ", e);
        }
        return EMPTY;
    }

    /** Returns for the input Node, the corresponding propertyName value as String object
     * 
     * If the value isNotBlank the input propertyName value is returned
     *
     * If is blank the defaultValue is returned
     *
     * @param node the Node
     * @param propertyName the property name
     * @return a String */
    public static String getStringPropertyFromNode(final Node node, final String propertyName, final String defaultValue) {
        final String result = getStringPropertyFromNode(node, propertyName);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return defaultValue;
    }

    /** Returns for the input Node, the corresponding propertyName value as String object
     *
     * If the value isNotBlank the input propertyName value is returned
     *
     * If is blank the corresponding propertyNameAlternative value is returned if isNotBlank
     *
     * If both are blank the defaultValue is returned
     *
     * @param node the Node
     * @param propertyName the property name
     * @param propertyNameAlternative the alternative property name
     * @return a String */
    public static String getStringPropertyFromNode(final Node node, final String propertyName, final String propertyNameAlternative,
            final String defaultValue) {
        String result = getStringPropertyFromNode(node, propertyName);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        result = getStringPropertyFromNode(node, propertyNameAlternative);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return defaultValue;
    }

    /** Returns for the input Node, the corresponding propertyName value as Calendar object
     *
     * @param node the Node
     * @param propertyName the property name
     * @return a Calendar */
    public static Calendar getDatePropertyFromNode(final Node node, final String propertyName) {
        try {
            final Value value = getPropertyFromNode(node, propertyName);
            if (value != null) {
                return value.getDate();
            }
        } catch (Exception e) {
            LOGGER.debug("Error getDatePropertyFromNode: ", e);
        }
        return null;
    }

    /** Returns for the input Node, the corresponding propertyName value as long object
     *
     * @param node the Node
     * @param propertyName the property name
     * @return a long */
    public static long getLongPropertyFromNode(final Node node, final String propertyName) {
        try {
            final Value value = getPropertyFromNode(node, propertyName);
            if (value != null) {
                return value.getLong();
            }
        } catch (Exception e) {
            LOGGER.debug("Error getLongPropertyFromNode: ", e);
        }
        return 0L;
    }

    /** Returns for the input Node, the corresponding propertyName value as long object
     * 
     * If the value==0 the input defaultValue is returned
     *
     * @param node the Node
     * @param propertyName the property name
     * @return a long */
    public static long getLongPropertyFromNode(final Node node, final String propertyName, final long defaultValue) {
        long l = getLongPropertyFromNode(node, propertyName);
        if (l != 0) {
            return l;
        }
        return defaultValue;
    }

    /** Returns for the input Node, the corresponding jcr:data value as Binary object
     *
     * @param node the Node
     * @return a Binary */
    public static Binary getBinaryFromNode(final Node node) {
        Binary property = null;
        try {
            final Value propertyFromNode = getPropertyFromNode(node, Property.JCR_DATA);
            if (propertyFromNode != null) {
                property = propertyFromNode.getBinary();
            }
        } catch (Exception e) {
            LOGGER.debug("Error getBinaryFromNode: ", e);
        }
        return property;
    }

    /** Returns for the input Node, the corresponding propertyName value as boolean object
     *
     * @param node the Node
     * @param propertyName the property name
     * @return a boolean */
    public static boolean getBooleanPropertyFromNode(final Node node, final String propertyName) {
        try {
            final Value value = getPropertyFromNode(node, propertyName);
            if (value != null) {
                return value.getBoolean();
            }
        } catch (RepositoryException e) {
            LOGGER.debug("Error getBooleanPropertyFromNode: ", e);
        }
        return false;
    }

    /** Returns path of a node without throwing any Exception.
     * 
     * If node.getPath() would throw an exception, a null is returned
     * 
     * @param node the Node
     *
     * @return a String or null */
    public static String getNodePath(final Node node) {
        try {
            return node.getPath();
        } catch (Exception e) {
            LOGGER.error("Cannot get path for node!", e);
        }
        return null;
    }

    /** Returns true if the input resource does not exist, false if exists
     * 
     * @param resource to test
     * @return true if the input resource does not exist */
    public static boolean isNonExistingResource(final Resource resource) {
        if (resource == null) {
            return true;
        }
        return resource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING);
    }

    /** Given the parentNode, if hasNode(nodeName) the children nodeName is returned
     * 
     * if !hasNode(nodeName), the children is created with the input resourceType
     *
     * @param parentNode where to get/create the children-node
     * @param nodeName of the children-node to get/create
     * @param resourceType of the children-node
     *
     * @return created/existing children-node with input nodeName and input resourceType */
    public static Node getOrCreateNode(final Node parentNode, final String nodeName, final String resourceType) {
        Node result = null;
        try {
            if (parentNode.hasNode(nodeName)) {
                result = parentNode.getNode(nodeName);
                if (!getStringPropertyFromNode(result, SLING_RESOURCE_TYPE_PROPERTY, EMPTY).equals(resourceType)) {
                    throw new IllegalArgumentException("Node " + nodeName + " does not have the required resourceType " + resourceType);
                }
            } else {
                result = parentNode.addNode(nodeName);
                result.setProperty(SLING_RESOURCE_TYPE_PROPERTY, resourceType);
            }
        } catch (Exception e) {
            LOGGER.error("Error getOrCreateNode", e);
        }
        return result;
    }

    /** If the input node is using the specified template, returns true
     *
     * @param node node to check
     * @param templatePath template path to check with
     *
     * @return true if template is used by node, false instead */
    public static Boolean isNodeUsingTemplate(final Node node, final String templatePath) {
        boolean isUsingTemplate = false;
        try {
            isUsingTemplate = StringUtils.equals(node.getNode(JcrConstants.JCR_CONTENT).getProperty(NameConstants.PN_TEMPLATE).getString(),
                    templatePath);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot get property " + NameConstants.PN_TEMPLATE + ", or path for node", e);
        }
        return isUsingTemplate;
    }

}
