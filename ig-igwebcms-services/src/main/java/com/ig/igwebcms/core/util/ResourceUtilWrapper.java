package com.ig.igwebcms.core.util;

import com.day.cq.commons.jcr.JcrUtil;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.resource.JcrPropertyMap;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;

/**
 * This class introduce all the utility method relate to node and node properties.
 */
public final class ResourceUtilWrapper extends JcrUtil {

    /**
     * The ApplicationConstants TRUE.
     */
    private static final String TRUE = "true";

    /**
     * Hidden Constructor.
     */
    private ResourceUtilWrapper() {
        super();
    }

    /**
     * @param resourceResolver as input parameter for getting particular resource.
     * @param resourceURL      Url to a particular resource.
     * @return ValueMap it consist all the properties of a resource.
     */
    public static ValueMap getValueMap(final ResourceResolver resourceResolver, final String resourceURL) {

        Resource resource = resourceResolver.getResource(resourceURL);
        return ResourceUtil.getValueMap(resource);
    }

    /**
     * @param resource as input parameter for getting ValueMap object.
     * @return ValueMap it consist all the properties of a resource.
     */
    public static ValueMap getValueMap(final Resource resource) {

        return ResourceUtil.getValueMap(resource);
    }

    /**
     * Gets the property string.
     *
     * @param resource     Resource object for which the property value is to be retrieved.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property string
     */
    public static String getPropertyString(final Resource resource, final String name, final String defaultValue) {

        return ResourceUtil.getValueMap(resource).get(name, defaultValue);
    }

    /**
     * Gets the property.
     *
     * @param <T>          the generic type
     * @param node         the node
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    public static <T> T getProperty(final Node node, final String name, final T defaultValue) {

        return new JcrPropertyMap(node).get(name, defaultValue);
    }

    /**
     * Gets the property.
     *
     * @param node the node
     * @param name the name
     * @return the property
     */
    public static Property getProperty(final Node node, final String name) {

        try {
            if (node.hasProperty(name)) {
                return node.getProperty(name);
            }
        } catch (final RepositoryException repoException) {
            LoggerUtil.debugLog(JcrUtil.class, "Could not get property " + name + "': ", repoException);
        }
        return null;
    }

    /**
     * Gets the property boolean.
     *
     * @param node         the node
     * @param name         the name
     * @param defaultValue the default value
     * @return the property boolean
     */
    public static boolean getPropertyBoolean(final Node node, final String name, final boolean defaultValue) {

        try {
            final Property property = ResourceUtilWrapper.getProperty(node, name);
            if (property != null) {
                return PropertiesUtil.toBoolean(property.getValue(), false);
            }
        } catch (final RepositoryException repoException) {
            LoggerUtil.errorLog(JcrUtil.class, "Repository Exception", repoException);
        }
        return defaultValue;
    }


    /**
     * To boolean.
     *
     * @param input the input
     * @return true, if successful
     */
    public static boolean toBoolean(final Object input) {

        return PropertiesUtil.toBoolean(input, false);
    }

    /**
     * Safely save session.
     *
     * @param session Session
     */
    public static void saveSession(final Session session) {

        try {
            if (session != null && session.hasPendingChanges()) {
                session.refresh(true);
                session.save();
            }
        } catch (final RepositoryException repoException) {
            LoggerUtil.errorLog(JcrUtil.class, "Could not save session: ", repoException);
        }
    }

    /**
     * Gets the absolute node.
     *
     * @param session      the session
     * @param absolutePath the absolute path
     * @return the absolute node
     */
    public static Node getAbsoluteNode(final Session session, final String absolutePath) {

        try {
            if (!StringUtils.isEmpty(absolutePath) && !absolutePath.contains("=") && session.nodeExists(absolutePath)) {
                return session.getNode(absolutePath);
            }
        } catch (final NullPointerException nullPointerException) {
            LoggerUtil.errorLog(JcrUtil.class, "NullPointer Exception", nullPointerException);
        } catch (final RepositoryException repoException) {
            LoggerUtil.errorLog(JcrUtil.class, "Repository Exception", repoException);
        }
        return null;
    }

    /**
     * Gets the property string array.
     *
     * @param property the property
     * @return the property string array
     */
    public static String[] getPropertyStringArray(final Property property) {

        try {
            return PropertiesUtil.toStringArray(property.getValue());
        } catch (final RepositoryException repoException) {
            LoggerUtil.errorLog(JcrUtil.class, "Repository Exception", repoException);
        }
        return new String[0];
    }

    /**
     * Gets the property string array.
     *
     * @param node the node
     * @param name the name
     * @return the property string array
     */
    public static String[] getPropertyStringArray(final Node node, final String name) {

        return ResourceUtilWrapper.getPropertyStringArray(ResourceUtilWrapper.getProperty(node, name));
    }

    /**
     * Gets the property string.
     *
     * @param resource object from with the property string will be returned.
     * @param name     the name
     * @return the property string
     */
    public static String getPropertyString(final Resource resource, final String name) {

        return ResourceUtilWrapper.getPropertyString(resource, name, StringUtils.EMPTY);
    }

    /**
     * Safely set property on JCR node.
     *
     * @param node     Node
     * @param name     Property name
     * @param value    Value
     * @param autoSave Auto save?
     * @return True, if successful
     */
    public static boolean setProperty(final Node node, final String name, final Object value, final boolean autoSave) {
        try {
            JcrResourceUtil.setProperty(node, name, value);
            if (autoSave) {
                ResourceUtilWrapper.saveSession(node.getSession());
            }
            return true;
        } catch (final NullPointerException nullPointerException) {
            LoggerUtil.errorLog(JcrUtil.class, "Could not set property: {}", nullPointerException);
        } catch (final RepositoryException repoException) {
            LoggerUtil.errorLog(JcrUtil.class, "Could not set property: {}", repoException);
        }
        return false;
    }

    /**
     * @param resourceResolver as input parameter for getting particular resource.
     * @param resourceURL      Url to a particular resource.
     * @return Resource Iterator. it can be iterated to walk through all the child nodes.
     */
    public static Iterator<Resource> getListChildren(final ResourceResolver resourceResolver, final String resourceURL) {

        Resource resource = resourceResolver.getResource(resourceURL);
        return resourceResolver.listChildren(resource);
    }
}
