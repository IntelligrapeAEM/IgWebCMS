package com.ig.igwebcms.core.model;

import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.tags.DefineObjectsTag;
import com.ig.igwebcms.core.constants.ApplicationConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.engine.EngineConstants;
import org.slf4j.Logger;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 * Abstract bean with useful methods implemented: - getters for WCM API context
 * variables: page, component, style etc - getters for Sling API context
 * variables: resourseResolver, slingRequest etc - getters for JCR API context
 * variables: node, session
 * <p/>
 * Concrete implementations of component beans may extend this class.
 * <p/>
 * Example of usage: <jsp:useBean id="newsreaderBean"
 * class="com.sap.components.newsreader.beans.NewsReaderBean" />
 * <jsp:setProperty name="newsreaderBean" property="pageContext"
 * value="<%=pageContext%>"/>
 */
public abstract class AbstractComponentBean {

    /**
     * Reference Variable for getting PageContext Object.
     */
    private PageContext pageContext;

    /**
     * setPageContext is a setter method for pageContext Variable.
     *
     * @param pageContextObj A PageContext object.
     */

    public final void setPageContext(final PageContext pageContextObj) {

        this.pageContext = pageContextObj;
        setResponseHeaders(getResponse());
    }

    /**
     * This method is used to set the response header.
     *
     * @param response HttpServletResponse Object as a argument.
     */
    protected void setResponseHeaders(final HttpServletResponse response) {
        /* Empty Method */
    }

    /**
     * Method that is used for disabling the component using the Felix console
     * configuration. The service name must match with what is entered into the
     * configuration.
     *
     * @return String Value.
     */
    public abstract String getComponentName();

    /**
     * init component here.
     */
    public abstract void initComponent();

    /**
     * {@link PageContext}.
     *
     * @return PageContext Object.
     */
    public final PageContext getPageContext() {
        return pageContext;
    }

    /**
     * {@link ResourceResolver}.
     *
     * @return ResourceResolver Object from page context.
     */
    public final ResourceResolver getResourceResolver() {
        return (ResourceResolver) pageContext.getAttribute(ApplicationConstants.RESOURCE_RESOLVER);
    }

    /**
     * {@link Component}.
     *
     * @return Component Class Object from page context.
     */
    public final Component getComponent() {
        return (Component) pageContext.getAttribute(EngineConstants.FILTER_SCOPE_COMPONENT.toLowerCase());
    }

    /**
     * {@link Node}.
     *
     * @return Node Class Object from page context.
     */
    public final Node getCurrentNode() {
        return (Node) pageContext.getAttribute(ApplicationConstants.CURRENT_NODE);
    }

    /**
     * {@link Page}.
     *
     * @return Page Class Object from page context.
     */
    public final Page getCurrentPage() {
        return (Page) pageContext.getAttribute(DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME);
    }

    /**
     * @return logger from page context.
     */
    public final Logger getLog() {
        return (Logger) pageContext.getAttribute(SlingBindings.LOG);
    }

    /**
     * {@link ComponentContext}.
     *
     * @return ComponentContext Object from page context.
     */
    public final ComponentContext getComponentContext() {
        return (ComponentContext) pageContext.getAttribute(DefineObjectsTag.DEFAULT_COMPONENT_CONTEXT_NAME);
    }

    /**
     * {@link Design}.
     *
     * @return Design Object from page context.
     */
    public final Design getCurrentDesign() {
        return (Design) pageContext.getAttribute(DefineObjectsTag.DEFAULT_CURRENT_DESIGN_NAME);
    }

    /**
     * {@link Style}.
     *
     * @return Style Object from page context.
     */
    public final Style getCurrentStyle() {
        return (Style) pageContext.getAttribute(DefineObjectsTag.DEFAULT_CURRENT_STYLE_NAME);
    }

    /**
     * {@link EditContext}.
     *
     * @return EditContext Object from page context.
     */
    public final EditContext getEditContext() {
        return (EditContext) pageContext.getAttribute(DefineObjectsTag.DEFAULT_EDIT_CONTEXT_NAME);
    }

    /**
     * {@link Designer}.
     *
     * @return Designer Object from page context.
     */
    public final Designer getDesigner() {
        return (Designer) pageContext.getAttribute(DefineObjectsTag.DEFAULT_DESIGNER_NAME);
    }

    /**
     * {@link PageManager}.
     *
     * @return PageManager Object from page context.
     */
    public final PageManager getPageManager() {
        return (PageManager) pageContext.getAttribute(DefineObjectsTag.DEFAULT_PAGE_MANAGER_NAME);
    }

    /**
     * {@link InheritanceValueMap}.
     *
     * @return InheritanceValueMap Object from page context.
     */
    public final InheritanceValueMap getPageProperties() {
        return (InheritanceValueMap) pageContext.getAttribute(DefineObjectsTag.DEFAULT_PAGE_PROPERTIES_NAME);
    }

    /**
     * @return properties of component on current page (made in edit mode).
     */
    public final ValueMap getProperties() {
        return (ValueMap) pageContext.getAttribute(DefineObjectsTag.DEFAULT_PROPERTIES_NAME);
    }

    /**
     * {@link Resource}.
     *
     * @return Resource class Object from page context.
     */
    public final Resource getResource() {
        return (Resource) pageContext.getAttribute(SlingBindings.RESOURCE);
    }

    /**
     * {@link SlingScriptHelper}.
     *
     * @return SlingScriptHelper class object from page context.
     */
    public final SlingScriptHelper getSlingScriptHelper() {
        return (SlingScriptHelper) pageContext.getAttribute(SlingBindings.SLING);
    }

    /**
     * {@link SlingHttpServletRequest}.
     *
     * @return SlingHttpServletRequest class object from page context.
     */
    public final SlingHttpServletRequest getSlingRequest() {
        return (SlingHttpServletRequest) pageContext.getAttribute(ApplicationConstants.SLING_REQUEST);
    }

    /**
     * {@link SlingHttpServletResponse}.
     *
     * @return SlingHttpServletResponse class object from page context.
     */
    public final SlingHttpServletResponse getSlingResponse() {
        return (SlingHttpServletResponse) pageContext.getAttribute(ApplicationConstants.SLING_RESPONSE);
    }

    /**
     * {@link Session}.
     *
     * @return JCR session.
     */
    public final Session getSession() {
        return getResourceResolver().adaptTo(Session.class);
    }

    /**
     * {@link HttpServletRequest}.
     *
     * @return HttpServletRequest class object from page context.
     */
    public final HttpServletRequest getRequest() {
        return (HttpServletRequest) pageContext.getRequest();
    }

    /**
     * {@link HttpServletResponse}.
     *
     * @return HttpServletResponse class object from page context.
     */
    public final HttpServletResponse getResponse() {
        return (HttpServletResponse) pageContext.getResponse();
    }

    /**
     * {@link JspWriter}.
     *
     * @return JspWriter class object from page context.
     */
    public final JspWriter getOut() {
        return pageContext.getOut();
    }

    /**
     * Return the current WCMMode of the request.
     *
     * @return WCMMode class object from page context.
     */
    public final WCMMode getWcmMode() {
        return WCMMode.fromRequest(getRequest());
    }

    /**
     * Return an individual property value that has been configured through the
     * dialog. Pass a default value in case the property has not been set
     *
     * @param propertyName Name of property.
     * @param defaultValue Default value is property doesn't exist.
     * @return Property Value.
     */
    public final String getConfiguredProperty(final String propertyName,
                                              final String defaultValue) {

        return (getProperties() != null && getProperties().get(propertyName) != null)
                ? (String) getProperties().get(propertyName) : defaultValue;
    }

    /**
     * Return an culdesac design template property value that has been
     * configured through the design_dialog. Pass a default value in case the
     * property has not been set
     *
     * @param propertyName Name of property.
     * @param defaultValue Default value is property doesn't exist.
     * @return Property Value.
     */
    public final String getStyleProperty(final String propertyName,
                                         final String defaultValue) {

        return (getCurrentStyle() != null && getCurrentStyle().get(propertyName) != null)
                ? (String) getCurrentStyle().get(propertyName) : defaultValue;
    }

    /**
     * Return an culdesac design template property value that has been
     * configured through the design_dialog. Pass a default value in case the
     * property has not been set
     *
     * @param propertyName Name of property.
     * @param defaultValue Default value is property doesn't exist.
     * @return CurrentStyle class Object.
     */
    public final Object getStylePropertyObject(final String propertyName,
                                         final String defaultValue) {

        return (getCurrentStyle() != null && getCurrentStyle().get(propertyName) != null)
                ? getCurrentStyle().get(propertyName) : defaultValue;
    }

    /**
     * Return an individual property value that has been set for the component
     * in the .content.xml. Pass a default value in case the property has not
     * been set
     *
     * @param propertyName Name of property.
     * @param defaultValue Default value is property doesn't exist.
     * @return Component property Value.
     */
    public final String getComponentProperty(final String propertyName,
                                       final String defaultValue) {

        return (getComponent() != null && getComponent().getProperties() != null && getComponent().getProperties().get(propertyName) != null) ? (String) getComponent()
                .getProperties().get(propertyName) : defaultValue;
    }

    /**
     * @return unique component id.
     */
    public final String getComponentId() {

        String nodeIdentifier = "";
        try {
            nodeIdentifier = getCurrentNode().getIdentifier();
        } catch (RepositoryException repoExp) {
            nodeIdentifier = "";
        }
        return nodeIdentifier;
    }
}
