package com.ig.igwebcms.services;


import com.ig.igwebcms.core.logging.LoggerUtil;
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

public interface TagBodyParserService {
    /**
     * Method Will be called when this service is first loaded.
     *
     * @param componentContext ComponentContext class object.
     */
    void activate(ComponentContext componentContext) ;

    /**
     * This method parses the Tag body and replace the pattern with its value.
     *
     * @param body This argument is the html text inside the tag body.
     * @return string constant.
     */
    public String parseText(String body);
}
