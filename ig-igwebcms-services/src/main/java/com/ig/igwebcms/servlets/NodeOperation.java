package com.ig.igwebcms.servlets;

import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Iterator;

/**
 * This Servlet return a Json Object of Nodes under
 * jcr:content of a requesting page url with nodedata selector.
 */
@Component(immediate = true, enabled = true, metatype = true)
@Service(Servlet.class)
@Properties({
        @Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default", propertyPrivate = true),
        @Property(name = "sling.servlet.extensions", value = "json", propertyPrivate = true),
        @Property(name = "sling.servlet.selectors", value = "nodedata", propertyPrivate = true),
        @Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true),
        @Property(name = "service.description", value = "Getting Node data under jcr content in JSON Array of JSON Objects "),
        @Property(name = "firstParameter", value = "key", label = "First Parameter", description = "First Parameter to be fetched from node"),
        @Property(name = "secondParameter", value = "value", label = "Second Parameter", description = "Second Parameter to be fetched from node")
})
public class NodeOperation extends SlingSafeMethodsServlet {
    /**
     * key is a string used as first parameter to be fetched from
     * the requesting page Node.
     */
    private String key = "";
    /**
     * value is a string used as second parameter to be fetched from
     * the requesting page Node.
     */
    private String value = "";

    /**
     * ResourceResolverFactory Service is used.
     */
    @Reference
    private ResourceResolverFactory resourceFactory;

    /**
     * Method will be called at the time when the servlet
     * is first loaded to felix console.
     *
     * @param componentContext ComponentContext object.
     */
    @Activate
    protected final void activate(final ComponentContext componentContext) {

        Dictionary properties = componentContext.getProperties();
        key = (String) properties.get("firstParameter");
        value = (String) properties.get("secondParameter");
    }

    /**
     * This method will be called when any of the property
     * related to this servlet changed using felix console configuration settings.
     *
     * @param componentContext ComponentContent object.
     */
    @Modified
    protected final void modified(final ComponentContext componentContext) {
        activate(componentContext);
    }

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Resource resource = request.getResource();
        String tempUrl = resource.getPath() + "/jcr:content";
        ResourceResolver resourceResolver = null;
        Resource res = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        Node node = null;
        try {
            resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
            Resource tempResource = resourceResolver.getResource(tempUrl);
            if (!ResourceUtil.isNonExistingResource(tempResource)) {
                Iterator<Resource> resourceIterator = tempResource.listChildren();
                while (resourceIterator.hasNext()) {
                    res = resourceIterator.next();
                    node = res.adaptTo(Node.class);
                    if (node != null) {
                        key = node.getProperty("key").getString();
                        value = node.getProperty("value").getString();
                        jsonObject = new JSONObject();
                        jsonObject.put("key", key);
                        jsonObject.put("value", value);
                        jsonObject.put("path", node.getPath().toString());
                        jsonArray.put(jsonObject);
                    }
                }
            }
            if (jsonArray.length() > 0) {
                out.print(jsonArray);
            } else {
                out.print("[]");
            }

        } catch (Exception e) {
            LoggerUtil.infoLog(NodeOperation.class, e.toString());
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }
}
