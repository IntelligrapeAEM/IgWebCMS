package com.ig.igwebcms.components;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.services.WeatherNodeUpdation;
import com.ig.igwebcms.services.WeatherService;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import java.io.Serializable;
import java.util.*;

/**
 * This is a scheduler class which repeatedly calls a weather service to
 * fetch the weather information.
 */
@Component(label = "WeatherScheduer", description = "fetches the weather details periodically and stores it in node",
        immediate = true, metatype = true, enabled = true)
public class WeatherScheduler {

    @Property(label = "Cron Expression", description = "Enter the cron expression to call the Weather Service",
            value = "0 0/1 * 1/1 * ? *")
    private static final String CRON_EXP = "cron.exp";

    @Reference
    Scheduler scheduler;

    @Reference
    WeatherService weatherService;

    @Reference
    WeatherNodeUpdation weatherNodeUpdation;

    /**
     * Activate method for WeatherScheduler component
     * @param componentContext
     */

    @Activate
    public void activate(ComponentContext componentContext) {

        LoggerUtil.infoLog(WeatherScheduler.class, "Weather Schdeuler================ Activate method called");

        Dictionary dictionary = componentContext.getProperties();

        String cronExp = (String) dictionary.get("cron.exp");
        Map<String, Serializable> config = new HashMap<String, Serializable>();
        boolean canRunConcurrently = true;
        String jobName = "WeatherJob";

       LoggerUtil.infoLog(WeatherScheduler.class, "cron expression is ==============" + cronExp);
       LoggerUtil.infoLog(WeatherScheduler.class, "weatherNodeUpdation object is" + weatherNodeUpdation);

        /** Creating the weather job  **/
        Runnable weatherJob = new Runnable() {
            @Override
            public void run() {
               LoggerUtil.infoLog(WeatherScheduler.class, "inside scheduler run method");
                synchronized (weatherNodeUpdation) {

                    HashMap<String, String> map = (HashMap) weatherNodeUpdation.parseNodes();
                   LoggerUtil.infoLog(WeatherScheduler.class, "Map received from parseNodes methods is" + map);
                    Set<String> keys = map.keySet();
                    Iterator<String> keysIterator = keys.iterator();
                    while (keysIterator.hasNext()) {
                        String cityName = keysIterator.next();
                       LoggerUtil.infoLog(WeatherScheduler.class, "city name passed to service is" + cityName);
                        String response = weatherService.getWeather(cityName);
                       LoggerUtil.infoLog(WeatherScheduler.class, "weather service response recieved" + response);
                        weatherNodeUpdation.updateWeatherDetails(response, map.get(cityName));
                       LoggerUtil.infoLog(WeatherScheduler.class, "Run method completed");
                    }
                }
            }
        };

        try {
           LoggerUtil.infoLog(WeatherScheduler.class, "job is being added===== inside try");
           LoggerUtil.infoLog(WeatherScheduler.class, "scheduler object" + scheduler);
            this.scheduler.addJob(jobName, weatherJob, config, cronExp, canRunConcurrently);
           LoggerUtil.infoLog(WeatherScheduler.class, "try completed");
        } catch (Exception e) {
           LoggerUtil.errorLog(WeatherScheduler.class, "Exception occured");
            e.printStackTrace();
        }
    }


    /**
     * Activate method for WeatherScheduler component
     * @param componentContext
     */

    @Modified
    public void update(ComponentContext componentContext) {
       LoggerUtil.infoLog(WeatherScheduler.class, "configuration Modified..so calling the activate method ");
        activate(componentContext);
    }

    /**
     * Deactivate method for WeatherScheduler component
     * @param componentContext
     */
    @Deactivate
    public void deactivate(ComponentContext componentContext) {
       LoggerUtil.infoLog(WeatherScheduler.class, "deactivate method called..removing the scheduler ");
        scheduler.removeJob("WeatherJob");
       LoggerUtil.infoLog(WeatherScheduler.class, "job removed");
    }


}

