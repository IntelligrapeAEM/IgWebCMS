package com.ig.igwebcms.servlets;

import com.ig.igwebcms.core.constants.ApplicationConstants;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * This servlet is used to get the json object of all child nodes
 * under jcr:content of the provided page jcr:content node url.
 */
@SlingServlet(methods = {"GET", "POST"}, paths = {"/bin/tag/body/operator/add.html"}, generateComponent = false)
@Component(description = "Node Description",
        enabled = true, immediate = true, metatype = true)
public class TagBodyParserServlet extends SlingAllMethodsServlet {

    /**
     * This method is used to check given string
     * & if it is null then it return empty string.
     *
     * @param data Input string value.
     * @return String value
     */
    private String checkNull(final String data) {
        return (data != null) ? data : "";
    }

    /**
     * ResourceResolverFactory object.
     */
    @Reference
    private ResourceResolverFactory resourceFactory;

    @Override
    protected final void doGet(final SlingHttpServletRequest request,
                               final SlingHttpServletResponse response) throws ServletException,
            IOException {
        PrintWriter out = response.getWriter();
        String nodeLocation = checkNull(request.getParameter("location"));
        ResourceResolver resourceResolver = null;
        String key = "", value = "", location = "";
        try {
            resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
            Resource resource = resourceResolver.getResource(nodeLocation);
            if (!ResourceUtil.isNonExistingResource(resource)) {
                Iterator<Resource> list = resource.listChildren();
                JSONObject object = null;
                Resource tempResource = null;
                ValueMap properties = null;
                JSONArray jsonArray = new JSONArray();
                while (list.hasNext()) {
                    tempResource = list.next();
                    properties = ResourceUtil.getValueMap(tempResource);
                    key = properties.get("key", "");
                    value = properties.get("value", "");
                    location = properties.get("location", "");
                    if (!key.equals("")) {
                        object = new JSONObject();
                        object.put("key", key);
                        object.put("value", value);
                        object.put("location", location);
                        jsonArray.put(object);

                    }
                }
                out.print(jsonArray);
            }
        } catch (Exception e) {
            LoggerUtil.infoLog(TagBodyParserServlet.class, "TagBodyParserServlet generates error - " + e.getMessage());
        } finally {
            resourceResolver.close();
        }
    }

    @Override
    protected final void doPost(final SlingHttpServletRequest request,
                                final SlingHttpServletResponse response) throws ServletException,
            IOException {
        String key = checkNull(request.getParameter("key"));
        String value = checkNull(request.getParameter("value"));
        String nodeLocation = checkNull(request.getParameter("location"));
        String operation = checkNull(request.getParameter("operation"));
        if (operation.equals(ApplicationConstants.DELETE)) {
            delete(nodeLocation);
        } else if (operation.equals(ApplicationConstants.EDIT)) {
            edit(key, value, nodeLocation);
        } else {
            add(key, value, nodeLocation);
        }
    }

    /**
     * This method is used to delete a child node under page jcr:content node.
     *
     * @param nodeLocation This argument is the path to the jcr:content node.
     * @return true if node deleted successfully.
     */
    private boolean delete(final String nodeLocation) {

        ResourceResolver resourceResolver = null;
        boolean result = false;
        try {
            resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            Item item = session.getItem(nodeLocation);
            item.remove();
            session.save();
            session.logout();
            result = true;
        } catch (Exception e) {
            LoggerUtil.infoLog(TagBodyParserServlet.class, "TagBodyParserServlet generates error - " + e.getMessage());
            result = false;
        } finally {
            resourceResolver.close();
        }
        return result;
    }

    /**
     * This method is used to edit a child node under page jcr:content node.
     * @param key is the key value to identify the node.
     * @param value is the new value for this key.
     * @param nodeLocation location of the node.
     */
    private void edit(final String key, final String value, final String nodeLocation) {
        ResourceResolver resourceResolver = null;
        if (!key.equals("")) {
            try {
                resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
                Session session = resourceResolver.adaptTo(Session.class);
                Resource resource = resourceResolver.getResource(nodeLocation);
                if (!ResourceUtil.isNonExistingResource(resource)) {
                    Node node = session.getNode(nodeLocation);
                    node.setProperty("key", key);
                    node.setProperty("value", value);

                    session.save();
                    session.logout();
                }
            } catch (Exception e) {
                LoggerUtil.infoLog(TagBodyParserServlet.class, "TagBodyParserServlet generates error - " + e.getMessage());
            } finally {
                resourceResolver.close();
            }
        } else {
            delete(nodeLocation);
        }
    }

    /**
     * This method is used to add child node under jcr:content with a name as
     * the key param.
     * @param key It is the name as well as a property of that node.
     * @param value Value property of that node.
     * @param nodeLocation Location of the node.
     */
    private void add(final String key, final String value, final String nodeLocation) {
        ResourceResolver resourceResolver = null;
        if (!key.equals("")) {
            try {
                resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
                Session session = resourceResolver.adaptTo(Session.class);
                Resource resource = resourceResolver.getResource(nodeLocation);
                if (!ResourceUtil.isNonExistingResource(resource)) {
                    Resource tempResource = resourceResolver.getResource(nodeLocation + "/" + key);
                    if (tempResource == null) {
                        Node node = session.getNode(nodeLocation);
                        Node newNode = node.addNode(key);
                        newNode.setProperty("key", key);
                        newNode.setProperty("value", value);
                        newNode.setProperty("location", nodeLocation + "/" + key);
                        session.save();
                    }
                    session.logout();
                }

            } catch (Exception e) {
                LoggerUtil.infoLog(TagBodyParserServlet.class, "TagBodyParserServlet generates error -" + e.getMessage());
            } finally {
                resourceResolver.close();
            }
        }
    }
}
