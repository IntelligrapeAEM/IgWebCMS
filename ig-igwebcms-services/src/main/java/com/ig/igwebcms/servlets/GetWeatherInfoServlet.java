package com.ig.igwebcms.servlets;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: intelligrape
 * Date: 8/3/14
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */

@Component(description = "Weather Info",
        enabled = true, immediate = true, metatype = true)
@Service(Servlet.class)
@Properties({
        @Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default", propertyPrivate = true),
        @Property(name = "sling.servlet.extensions", value = "json", propertyPrivate = true),
        @Property(name = "sling.servlet.selectors", value = "weatherdata", propertyPrivate = true),
        @Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true),
        @Property(name = "service.description", value = "Getting Node data under jcr content in JSON Array of JSON Objects "),
        @Property(name = "firstParameter", value = "jcr:title", label = "First Parameter", description = "First Parameter to be fetched from node"),
        @Property(name = "secondParameter", value = "path", label = "Second Parameter", description = "Second Parameter to be fetched from node")
})
public class GetWeatherInfoServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(GetWeatherInfoServlet.class);
    private static final String weatherNodeLocation = "/etc/igwebcms/WeatherInfo";

    private String key = "";
    private String value = "";


    @Reference
    ResourceResolverFactory resolverFactory;

    @Activate
    protected void activate(ComponentContext componentContext) {
        Dictionary properties = componentContext.getProperties();
        key = (String) properties.get("firstParameter");
        value = (String) properties.get("secondParameter");
    }

    @Modified
    protected void modified(ComponentContext componentContext) {
        activate(componentContext);
    }

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
            IOException {
        log.info("inside servlet");
        PrintWriter out = response.getWriter();
        Resource resource = request.getResource();
        String prop = request.getParameter("result");
        log.info("prop got result" + prop);
        log.info("resource called" + resource);

        ResourceResolver resourceResolver = null;
        Resource res = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        Node node = null;
        String tempUrl = "";
        try {
            resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
            if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
                log.info("inside if");

                if (prop != null && prop.equalsIgnoreCase("city")) {
                    log.info("prop got" + prop);
                    tempUrl = resource.getPath() + "/jcr:content/par";
                    log.info("city url path is" + tempUrl);
                    Resource cityResource = resource.getChild(tempUrl);
                    log.info("city resource resource got is" + cityResource);
                    if (cityResource != null && !ResourceUtil.isNonExistingResource(cityResource)) {
                        log.info("inside if for city nodes");
                        Iterator<Resource> cityNodes = cityResource.listChildren();
                        while (cityNodes.hasNext()) {
                            Resource cityNodeComp = cityNodes.next();
                            ValueMap cityValueMap = cityNodeComp.adaptTo(ValueMap.class);
//                                    String cityName=(String)cityValueMap.get("city");
                            log.info("map is" + cityValueMap);
                            key = (String) cityValueMap.get("city");
                            log.info("key is" + key);
                            value = cityNodeComp.getPath();
                            log.info("val is" + value);
                            jsonObject = new JSONObject();
                            jsonObject.put("text", key);
                            jsonObject.put("value", value);
                            log.info("json obj is" + jsonObject);
                            jsonArray.put(jsonObject);

                        }
                    }
                } else if (prop == null) {

                    Iterator<Resource> resourceIterator = resource.listChildren();
                    while (resourceIterator.hasNext()) {
                        res = resourceIterator.next();
                        log.info("res got is" + res);
                        if (!(res.getPath().endsWith("/jcr:content"))) {
                            tempUrl = res.getPath() + "/jcr:content";
                            log.info("tempurl is" + tempUrl);
                            Resource tempResource = resourceResolver.getResource(tempUrl);
                            log.info("temp res is" + tempResource);
//                    Iterator<Resource> tempResourceIterator = tempResource.listChildren();
//                    while (tempResourceIterator.hasNext()) {

//                        res = tempResourceIterator.next();
                            ValueMap map = tempResource.adaptTo(ValueMap.class);
                            log.info("map is" + map);
//                        if (node != null) {
                            key = (String) map.get("jcr:title");
                            log.info("key is" + key);

                            value = res.getPath();
                            log.info("val is" + value);
                            jsonObject = new JSONObject();
                            jsonObject.put("text", key);
                            jsonObject.put("value", value);
//                        jsonObject.put("path", node.getPath().toString());
                            log.info("json obj is" + jsonObject);
                            jsonArray.put(jsonObject);

                        }
//                        }

                    }
                }
            }
            log.info("jsonArr is" + jsonArray);
            if (jsonArray.length() > 0)
                out.print(jsonArray);
            else
                out.print("[]");

            log.info("try finished");

        } catch (Exception e) {
            log.info("inside catch.. Exception occured " + e.toString());
            out.print("[]");
        } finally {
            if (resourceResolver != null)
                resourceResolver.close();
        }
    }


    private Map<String, String> parseNodes() {

        log.info("Inside parseNodes() method");
        ResourceResolver resourceResolver = null;
        HashMap<String, String> map = new HashMap();
        try {
            log.info("weather node location is" + weatherNodeLocation);
            resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
            Resource resource = resourceResolver.getResource(weatherNodeLocation);
            log.info("resource got is" + resource);
            if (!ResourceUtil.isNonExistingResource(resource)) {

                Iterator<Resource> countryResourceIterator = resource.listChildren();

                Resource countryResource = null;
                Resource stateResource = null;
                while (countryResourceIterator.hasNext()) {
                    countryResource = countryResourceIterator.next();
                    log.info("country resource resource got is" + countryResource);
                    Iterator<Resource> stateResourceIterator = countryResource.listChildren();
                    while (stateResourceIterator.hasNext()) {
                        stateResource = stateResourceIterator.next();
                        log.info("state resource resource got is" + stateResource);
                        String path = stateResource.getPath() + "/jcr:content/par";
                        log.info("path is" + path);
                        Resource cityResource = stateResource.getChild(path);
                        log.info("city resource resource got is" + cityResource);
                        if (cityResource != null && !ResourceUtil.isNonExistingResource(cityResource)) {
                            log.info("inside if for city nodes");
                            Iterator<Resource> cityNodes = cityResource.listChildren();
                            while (cityNodes.hasNext()) {
                                Resource cityNodeComp = cityNodes.next();
                                ValueMap cityValueMap = cityNodeComp.adaptTo(ValueMap.class);
                                String cityName = (String) cityValueMap.get("city");
                                map.put(cityName, cityNodeComp.getPath());
                                log.info("node map got is " + map);
                            }
                        }
                    }
                }

            }
            log.info("final map generated is" + map);
            return map;

        } catch (LoginException e) {
            log.error("Exception occured while adding the node");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            log.info("exception occured catch3");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            e.getCause();
        } finally {
            log.info("inside finally");
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
        return map;
    }

}
