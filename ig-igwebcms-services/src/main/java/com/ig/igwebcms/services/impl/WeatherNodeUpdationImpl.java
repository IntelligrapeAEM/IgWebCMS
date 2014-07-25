package com.ig.igwebcms.services.impl;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.services.WeatherNodeUpdation;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *   This service provides method for node updation
 */
@Component(label = "WeatherNodeupdation", description = "fetches the weather details and stores it in node",
        immediate = true, metatype = true, enabled = true)
@Service(WeatherNodeUpdation.class)
public class WeatherNodeUpdationImpl implements WeatherNodeUpdation {

    /** Path where to search  for city information **/
    private static final String weatherNodeLocation = "/etc/igwebcms/WeatherInfo";

    @Reference
    ResourceResolverFactory resolverFactory;

    /**
     * This method parses the weather information recieved from weather service
     * and fetches the temperature details
     * @param weatherInfo is the weather info recieved from service
     * @return
     */
    public HashMap<String, String> parseWeatherInfo(String weatherInfo) {
        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"Inside parseWeatherInfo method");
        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"received weather info" + weatherInfo);
        try {
            JSONObject weatherJSON = new JSONObject(weatherInfo);
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"converted json is " + weatherJSON);
            HashMap<String, String> map = new HashMap<String, String>();
            String temp = weatherJSON.getJSONObject("main").get("temp").toString();
            String city = weatherJSON.get("name").toString();
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"String received city and temp" + city + "   " + temp);
            map.put("city", city);
            map.put("temp", temp);
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"parseWeatherInfo map is " + map);
            return map;
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    /**
     * This method parses the nodes
     * @return
     */
    @Override
    public Map<String, String> parseNodes() {

        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"Inside parseNodes() method");
        ResourceResolver resourceResolver = null;
        HashMap<String, String> map = new HashMap();
        try {
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"weather node location is" + weatherNodeLocation);
            resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
            Resource resource = resourceResolver.getResource(weatherNodeLocation);
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"resource got is" + resource);
            if (!ResourceUtil.isNonExistingResource(resource)) {

                Iterator<Resource> countryResourceIterator = resource.listChildren();

                Resource countryResource = null;
                Resource stateResource = null;
                while (countryResourceIterator.hasNext()) {
                    countryResource = countryResourceIterator.next();
                    LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"country resource resource got is" + countryResource);
                    Iterator<Resource> stateResourceIterator = countryResource.listChildren();
                    while (stateResourceIterator.hasNext()) {
                        stateResource = stateResourceIterator.next();
                        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"state resource resource got is" + stateResource);
                        String path = stateResource.getPath() + "/jcr:content/par";
                        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"path is" + path);
                        Resource cityResource = stateResource.getChild(path);
                        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"city resource resource got is" + cityResource);
                        if (cityResource != null && !ResourceUtil.isNonExistingResource(cityResource)) {
                            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"inside if for city nodes");
                            Iterator<Resource> cityNodes = cityResource.listChildren();
                            while (cityNodes.hasNext()) {
                                Resource cityNodeComp = cityNodes.next();
                                ValueMap cityValueMap = cityNodeComp.adaptTo(ValueMap.class);
                                String cityName = (String) cityValueMap.get("city");
                                map.put(cityName, cityNodeComp.getPath());
                                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"node map got is" + map);
                            }
                        }
                    }
                }

            }
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"final map generated is" + map);
            return map;

        } catch (LoginException e) {
            LoggerUtil.errorLog(WeatherNodeUpdationImpl.class,"Exception occured while adding the node");
            e.printStackTrace();
        } catch (Exception e) {
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"exception occured catch3");
            e.printStackTrace();
            e.getCause();
        } finally {
            LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"inside finally");
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
        return map;
    }

    /**
     * This method updates the weather information
     * @param response is the response recieved
     * @param resPath is the resource path
     */
    public void updateWeatherDetails(String response, String resPath) {

        LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"Inside adding node");
        ResourceResolver resourceResolver = null;
        HashMap<String, String> map = parseWeatherInfo(response);
        if (map != null && !map.isEmpty()) {

            Session session = null;
            try {
                resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
                session = resourceResolver.adaptTo(Session.class);
                Resource resource = resourceResolver.getResource(resPath);
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"node location is" + resPath);
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"resource got is" + resource);
                if (!ResourceUtil.isNonExistingResource(resource)) {
                    Node node = session.getNode(resPath);
                    LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"Node exists" + node);
                    node.setProperty("temp", map.get("temp"));
                }
                session.save();
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"node property saved at" + resPath);

            } catch (LoginException e) {
                LoggerUtil.errorLog(WeatherNodeUpdationImpl.class,"Exception occured while adding the node");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (PathNotFoundException e) {
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"exception occured catch1");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                e.getCause();
            } catch (RepositoryException e) {
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"exception occured catch2");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                e.getCause();
            } catch (Exception e) {
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"exception occured catch3");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                e.getCause();
            } finally {
                LoggerUtil.infoLog(WeatherNodeUpdationImpl.class,"inside finally");
                if (resourceResolver != null) {
                    resourceResolver.close();
                    if (session != null)
                        session.logout();
                }
            }
        }
    }
}
