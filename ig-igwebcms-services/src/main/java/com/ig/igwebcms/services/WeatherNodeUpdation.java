package com.ig.igwebcms.services;

import java.util.HashMap;
import java.util.Map;

/**
 *  This is an interface that declares method for Node updation
 */
public interface WeatherNodeUpdation {
    public HashMap<String, String> parseWeatherInfo(String weatherInfo);

    public void updateWeatherDetails(String response, String resPath);

    public Map<String, String> parseNodes();
}
