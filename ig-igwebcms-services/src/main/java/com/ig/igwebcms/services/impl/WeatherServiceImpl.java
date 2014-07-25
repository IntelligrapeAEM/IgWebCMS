package com.ig.igwebcms.services.impl;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.services.WeatherService;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import java.io.IOException;


/**
 * This class is to call the weather web service.
 */

@Component(immediate = true, metatype = true)
@Service(WeatherService.class)
@Properties({
        @Property(name = Constants.SERVICE_VENDOR, value = "IG"),
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "Provides weather information.")
})
public class WeatherServiceImpl implements WeatherService {


    /**
     * This method is to fetch the weather details for the city
     * @param city is the city name
     * @return
     */
    @Override
    public String getWeather(String city) {
        LoggerUtil.infoLog(WeatherServiceImpl.class,"inside weather service getWeather method");
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city;

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        String response;
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            byte[] responseBody = method.getResponseBody();
            response = new String(responseBody);
            LoggerUtil.infoLog(WeatherServiceImpl.class, "Response received from weather service" + response);
            return response;
        } catch (HttpException e) {
            LoggerUtil.errorLog(WeatherServiceImpl.class,"Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LoggerUtil.errorLog(WeatherServiceImpl.class, "Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return null;
    }


}


