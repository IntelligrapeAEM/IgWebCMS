package com.ig.igwebcms.taglib;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Created with IntelliJ IDEA.
 * User: intelligrape
 * Date: 11/3/14
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WeatherTag extends SimpleTagSupport {
    private static final Logger logger = LoggerFactory.getLogger(WeatherTag.class);

    @Override
    public void doTag() {
        try {                /* get page Attributes */
            logger.info("inside do tag");
            final PageContext pageContext = (PageContext) getJspContext();
            logger.info("pagecontext got is" + pageContext);
            ValueMap properties = (ValueMap) pageContext.getAttribute("properties");
            logger.info("properties got are" + properties);
//            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest)pageContext.getAttribute("slingRequest");
            ResourceResolver resourceResolver = (ResourceResolver) pageContext.getAttribute("resourceResolver");
            logger.info("resourceResolver got are" + resourceResolver);
            JspWriter out = getJspContext().getOut();
            String cityUrl = (String) properties.get("city");
            Resource resource = resourceResolver.getResource(cityUrl);
            String cityName = "";
            String temp = "";
            if (resource != null) {
                logger.info("resource got is" + resource);
                ValueMap map = resource.adaptTo(ValueMap.class);
                cityName = (String) map.get("city");
                temp = (String) map.get("temp");
            }
            logger.info("City and temp is" + cityName + " " + temp);
            out.print("City and temp is" + cityName + " " + temp);

        } catch (Exception e) {
            logger.info("exception occured");
        }
    }


}
