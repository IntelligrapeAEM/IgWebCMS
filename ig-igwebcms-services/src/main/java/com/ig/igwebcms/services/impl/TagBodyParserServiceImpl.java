package com.ig.igwebcms.services.impl;


import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.services.TagBodyParserService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.ComponentContext;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class works as a service for TagBodyParser servlet.
 * Mainly it check the tag body and parse a <code>@{location to the node having value property}</code>
 * and replace it with the value property present at this node location.
 */
@Component(enabled = true, immediate = true, metatype = true)
@Service(value = TagBodyParserService.class)
public class TagBodyParserServiceImpl implements TagBodyParserService {

    //@Property(name = REGEX, value = "\\@\\{(.*?)\\}", label = "Pattern for Data Matching", propertyPrivate = true)
    /**
     * String constant used in this class only.
     */
    private static String pattern = "";

    /**
     * ResourceResolverFactory service reference.
     */
    @Reference
    private ResourceResolverFactory resolverFactory;

    /**
     * Method Will be called when this service is first loaded.
     *
     * @param componentContext ComponentContext class object.
     */
    @Activate
    public final void activate(final ComponentContext componentContext) {
        Dictionary properties = componentContext.getProperties();
//        pattern = (String) properties.get(ApplicationConstants.REGEX);
        pattern = "\\@\\{(.*?)\\}";
    }

    /**
     * This method parses the Tag body and replace the pattern with its value.
     *
     * @param tagBody This argument is the html text inside the tag body.
     * @return string constant.
     */
    public final String parseText(final String tagBody) {
        String body = tagBody;
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile(pattern).matcher(body);
        while (m.find()) {
            matches.add(m.group(1));
        }

        if (!matches.isEmpty()) {
            ResourceResolver resourceResolver = null;
            try {
                resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
                Resource resource = null;
                ValueMap properties = null;
                String value = "";
                String[] tempUrl;
                for (String url : matches) {
                    resource = null;
                    properties = null;
                    value = "";
                    tempUrl = url.split("\\.");
                    if (tempUrl.length == 2) {
                        resource = resourceResolver.getResource(tempUrl[0]);
                        properties = ResourceUtil.getValueMap(resource);
                        value = properties.get(tempUrl[1], "");
                    }
                    body = body.replace("@{" + url + "}", value);
                }

            } catch (Exception e) {
                LoggerUtil.infoLog(TagBodyParserService.class, "TAG Body Parser Service Error Report =" + e.getMessage());
            } finally {
                if (resourceResolver != null) {
                    resourceResolver.close();
                }
            }
        }
        return body;
    }
}
