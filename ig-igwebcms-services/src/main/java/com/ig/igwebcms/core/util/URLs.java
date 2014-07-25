package com.ig.igwebcms.core.util;

import com.day.cq.wcm.api.Page;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public final class URLs {

    private URLs() {
    }

    /**
     * Resolves a url string.
     * If the URL string resolves to a cq Page, then ".html" is appended to url otherwise url is returned.
     *
     * @param resolver ResourceResolver to use in the resolution process
     * @param tempUrl  The url to resolve
     * @return If the URL string resolves to a cq Page, then ".html" is appended to url otherwise url is returned.
     */
    public static String resolvePages(final ResourceResolver resolver, final String tempUrl) {

        String url = tempUrl;
        if (resolver != null) {
            final Resource resource = resolver.getResource(url);
            final Page page = resource.adaptTo(Page.class);
            if (page != null) {
                url += ".html";
            }
        }
        LoggerUtil.infoLog(URLs.class, "Returning url =>" + url);
        return url;
    }

    /* If the URL is an internal pages  true is returned otherwise false
     *
     * @param url The url
     * @return Boolean value true for internal page and false for external page
     */
    public static Boolean isInternalURL(final String url) {
        LoggerUtil.infoLog(URLs.class, "URL----" + url);
//        return  !(url.startsWith("http://")|| url.startsWith("https://")) ? true:  false;
        return true;
    }
}
