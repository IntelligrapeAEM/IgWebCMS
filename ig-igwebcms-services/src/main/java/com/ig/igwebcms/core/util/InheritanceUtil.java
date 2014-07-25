package com.ig.igwebcms.core.util;

import com.day.cq.wcm.api.Page;
import com.ig.igwebcms.core.constants.ApplicationConstants;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

/**
 * This class provides all utility methods related to inheritance tree property access in the current page.
 * i.e. if we want to get a property from parent node & this property is already exist in current page property.
 */

public final class InheritanceUtil {

    /**
     * Hidden constructor.
     */
    private InheritanceUtil() {
        /*  NO CONSTRUCTOR*/
    }

    /**
     * Inherit a property from parent pages if property on current page not set.
     *
     * @param page         Current page
     * @param propertyName Property name
     * @return Inherited Property
     */
    public static Property inheritProperty(final Page page, final String propertyName) {
        Property property = null;
        try {
            final Resource contentResource = page.getContentResource();
            final Node contentNode = contentResource.adaptTo(Node.class);
            if (contentNode.hasProperty(propertyName)) {
                property = contentNode.getProperty(propertyName);
            } else if (page.getDepth() > ApplicationConstants.LANGUAGE_PAGE_LEVEL) {
                property = InheritanceUtil.inheritProperty(page.getParent(), propertyName);
            }
        } catch (RepositoryException e) {
            LoggerUtil.debugLog(InheritanceUtil.class, "Could not inherit property: ", e);
        }
        return property;
    }

    /**
     * Inherit property string from parent pages.
     *
     * @param page         Current page
     * @param propertyName Property name
     * @param defaultValue Default value
     * @return Inherited value or default value
     */
    public static String inheritPropertyString(final Page page, final String propertyName, final String defaultValue) {
        final Property property = InheritanceUtil.inheritProperty(page, propertyName);
        String propertyValue = defaultValue;
        try {
            if (property != null) {
                propertyValue = property.getString();
            }
        } catch (RepositoryException repoException) {
            LoggerUtil.debugLog(InheritanceUtil.class, "Couldn't get string from property: {}", repoException.getMessage());
        }
        return propertyValue;
    }

    /**
     * Inherit property string from parent pages (return null if not set).
     *
     * @param page         Current page
     * @param propertyName Property name
     * @return Inherited value
     */
    public static String inheritPropertyString(final Page page, final String propertyName) {
        return InheritanceUtil.inheritPropertyString(page, propertyName, null);
    }

    /**
     * Inherit boolean property value from parent pages.
     *
     * @param page         Current page
     * @param propertyName Property name
     * @param defaultValue Default value
     * @return Inherited value or default value
     */
    public static Boolean inheritPropertyBoolean(final Page page, final String propertyName, final Boolean defaultValue) {
        final Property property = InheritanceUtil.inheritProperty(page, propertyName);
        boolean propertyValue = defaultValue;
        try {
            propertyValue = property.getBoolean();
        } catch (RepositoryException repositoryException) {
            LoggerUtil.debugLog(InheritanceUtil.class, "Couldn't get boolean from property: {}", repositoryException);
        }
        return propertyValue;
    }

    /**
     * Inherit a node (below content-node) from parent pages if node on current
     * page not set.
     *
     * @param page    Current page
     * @param relPath Relative path to node (starting point is jcr:content-node)
     * @return Inherited node
     */
    public static Node inheritNode(final Page page, final String relPath) {
        Node node = null;
        try {
            final Resource contentResource = page.getContentResource();
            final Node contentNode = contentResource.adaptTo(Node.class);
            if (contentNode.hasNode(relPath)) {
                node = contentNode.getNode(relPath);
            } else if (page.getDepth() > ApplicationConstants.LANGUAGE_PAGE_LEVEL) {
                node = InheritanceUtil.inheritNode(page.getParent(), relPath);
            }
        } catch (RepositoryException e) {
            LoggerUtil.debugLog(InheritanceUtil.class, "Could not inherit node: ", e);
        }
        return node;
    }

    /**
     * Inherit a node (below content-node) from parent pages
     * if node on current page not set, and return it's path.
     *
     * @param page        Current page
     * @param relPath     Relative path to node (starting point is jcr:content-node)
     * @param defaultPath Default path if node not found
     * @return Path of inherited node
     */
    public static String inheritNodePath(final Page page, final String relPath, final String defaultPath) {
        String nodePath = defaultPath;
        try {
            final Node inheritedNode = InheritanceUtil.inheritNode(page, relPath);
            if (inheritedNode != null) {
                nodePath = inheritedNode.getPath();
            }
        } catch (RepositoryException e) {
            LoggerUtil.debugLog(InheritanceUtil.class, "Could not inherit node path: ", e);
        }
        return nodePath;
    }

    /**
     * Inherit a node (below content-node) from parent pages
     * if node on current page not set, and return it's path.
     *
     * @param page    Current page
     * @param relPath Relative path to node (starting point is jcr:content-node)
     * @return Path of inherited node (default is empty)
     */
    public static String inheritNodePath(final Page page, final String relPath) {
        return InheritanceUtil.inheritNodePath(page, relPath, "");
    }

}
