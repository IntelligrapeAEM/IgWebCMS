package com.ig.igwebcms.core.helper;

import com.day.cq.commons.PathInfo;
import com.day.cq.widget.HtmlLibrary;
import com.day.cq.widget.HtmlLibraryManager;
import com.day.cq.widget.LibraryType;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class updates the links for versioning the clientlibs.
*/
public class LinkGetter {
    private Pattern htmlTag;
    private Pattern link;
    private Pattern scriptTag;
    private Pattern src;

    /**
     * constructor LinkGetter() that sets the patterns for
     * searching the <link> and <script> tags
     */
    public LinkGetter() {
        htmlTag = Pattern.compile("<link\\b[^>]*href=\"[^>]*>(.*?)");
        link = Pattern.compile("href=\"[^>]*\">");
        scriptTag = Pattern.compile("<script\\b[^>]*src=\"[^>]*>(.*?)</script>");
        src = Pattern.compile("src=\"[^>]*\">");
    }

    /**
     * This method checks for <Link> tags and  updates the href with the md5Hex selector
     * @param pageContext is the PageContext
     * @param str is the <link> </link> tag
     * @return the list of updated <link></link> tags
     */
    public List<String> getLinks(PageContext pageContext, String str) {
        List<String> links = new ArrayList<String>();
        LoggerUtil.infoLog(LinkGetter.class,"reached inside method");

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute("sling");
        HtmlLibraryManager htmlLibraryManager = (HtmlLibraryManager) sling.getService(HtmlLibraryManager.class);
        SlingHttpServletRequest request = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
        Logger log = (Logger) pageContext.getAttribute("log");
        LibraryType libraryType = LibraryType.CSS;
        StringBuffer s = new StringBuffer(str);
        Matcher tagmatch = htmlTag.matcher(s);
        while (tagmatch.find()) {
            LoggerUtil.infoLog(LinkGetter.class,"matches");
            Matcher matcher = link.matcher(tagmatch.group());
            LoggerUtil.infoLog(LinkGetter.class,"First group " + matcher.group());
            String link = matcher.group().replaceFirst("href=\"", "")
                    .replaceFirst("\".*type=\".*", "");
            LoggerUtil.infoLog(LinkGetter.class,"link generated is" + link);
            PathInfo pathInfo = new PathInfo(link);
            LoggerUtil.infoLog(LinkGetter.class,"pathinfo is====" + pathInfo);
            StringBuffer linkBuf = new StringBuffer();
            HtmlLibrary htmlLibrary = htmlLibraryManager.getLibrary(libraryType, pathInfo.getResourcePath());
            LoggerUtil.infoLog(LinkGetter.class,"lib is====" + htmlLibrary);
            if (htmlLibrary != null) {
                LoggerUtil.infoLog(LinkGetter.class,"html library ========" + htmlLibrary.getLibraryPath() + "." + htmlLibrary.getLastModified() + libraryType.extension);
                linkBuf.append(htmlLibrary.getLibraryPath() + ".");
                try {
                    linkBuf.append(DigestUtils.md5Hex(htmlLibrary.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                linkBuf.append(libraryType.extension);
            } else {
                LoggerUtil.debugLog(LinkGetter.class,"Could not find HtmlLibrary at path: {}" + pathInfo.getResourcePath());
            }
            links.add(linkBuf.toString());
        }
        return links;
    }

    /**
     * This method calls the getLinks() method and updates the <link></link> tags
     * @param pageContext is the PageContext
     * @param str is the <link></link> tags list
     * @return the list of updated <link></link>  tags
     */
    public List<String> getVersionedLinkTags(PageContext pageContext, String str) {

        List<String> hrefList = getLinks(pageContext, str);

        List<String> linkList = new ArrayList<String>();
        if (hrefList != null) {
            Iterator itr = hrefList.iterator();
            while (itr.hasNext()) {
                StringBuffer s = new StringBuffer(str);
                Matcher tagmatch = htmlTag.matcher(s);
                while (tagmatch.find()) {
                    LoggerUtil.infoLog(LinkGetter.class,"matches");
                    Matcher matcher = link.matcher(tagmatch.group());
                    LoggerUtil.infoLog(LinkGetter.class,"First group " + matcher.group());
                    String link = tagmatch.group().replaceFirst("href=\".*\"", "href=\"\"").
                            replaceFirst("href=\"", "href=\"" + itr.next() + "\" type=\"text/css");

                    LoggerUtil.infoLog(LinkGetter.class,"link generated is" + link);

                    linkList.add(link);
                }

            }
        }
        return linkList;
    }

    /**
     * This method checks the <Script></Script> tags  and  updates the href with the md5Hex selector
     * @param pageContext is the PageContext
     * @param str is the <Script></Script> tags list
     * @return the list of updated <Script></Script> tags
     */
    public List<String> getSrc(PageContext pageContext, String str) {
        List<String> links = new ArrayList<String>();
        LoggerUtil.infoLog(LinkGetter.class,"reached inside method");

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute("sling");
        HtmlLibraryManager htmlLibraryManager = (HtmlLibraryManager) sling.getService(HtmlLibraryManager.class);
        SlingHttpServletRequest request = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
        Logger log = (Logger) pageContext.getAttribute("log");
        LibraryType libraryType = LibraryType.JS;
        StringBuffer s = new StringBuffer(str);
        Matcher tagmatch = scriptTag.matcher(s);
        while (tagmatch.find()) {
            LoggerUtil.infoLog(LinkGetter.class,"matches");
            Matcher matcher = src.matcher(tagmatch.group());
            LoggerUtil.infoLog(LinkGetter.class,"First group " + matcher.group());
            String link = matcher.group().replaceFirst("src=\"", "")
                    .replaceFirst("\".*type=\".*", "").replaceFirst("\">", "");
            LoggerUtil.infoLog(LinkGetter.class,"link generated is" + link);
            PathInfo pathInfo = new PathInfo(link);
            LoggerUtil.infoLog(LinkGetter.class,"pathinfo is====" + pathInfo);
            StringBuffer linkBuf = new StringBuffer();
            HtmlLibrary htmlLibrary = htmlLibraryManager.getLibrary(libraryType, pathInfo.getResourcePath());
            LoggerUtil.infoLog(LinkGetter.class,"lib is====" + htmlLibrary);
            if (htmlLibrary != null) {
                LoggerUtil.infoLog(LinkGetter.class,"html library ========" + htmlLibrary.getLibraryPath() + "." + htmlLibrary.getLastModified() + libraryType.extension);
                linkBuf.append(htmlLibrary.getLibraryPath() + ".");
                try {
                    linkBuf.append(DigestUtils.md5Hex(htmlLibrary.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                linkBuf.append(libraryType.extension);
                LoggerUtil.infoLog(LinkGetter.class,"linkbuf is" + linkBuf);
            } else {
                LoggerUtil.debugLog(LinkGetter.class,"Could not find HtmlLibrary at path: {}" + pathInfo.getResourcePath());
                LoggerUtil.infoLog(LinkGetter.class,"Could not find HtmlLibrary at path: {}" + pathInfo.getResourcePath());
            }
            links.add(linkBuf.toString());
        }
        return links;
    }

    /**
     * This method calls the getSrc() method and updates the <Script></Script> tags
     * @param pageContext is the PageContext
     * @param str is the <Script></Script> tag
     * @return the list of updated <Script></Script> tags
     */
    public List<String> getVersionedScriptTags(PageContext pageContext, String str) {

        List<String> hrefList = getSrc(pageContext, str);
        List<String> linkList = new ArrayList<String>();
        if (hrefList != null) {
            Iterator itr = hrefList.iterator();
            while (itr.hasNext()) {
                StringBuffer s = new StringBuffer(str);
                Matcher tagmatch = scriptTag.matcher(s);
                while (tagmatch.find()) {
                    LoggerUtil.infoLog(LinkGetter.class,"matches");
                    Matcher matcher = src.matcher(tagmatch.group());
                    LoggerUtil.infoLog(LinkGetter.class,"First group " + matcher.group());
                    String link = tagmatch.group().replaceFirst("src=\".*\"", "src=\"\"").
                            replaceFirst("src=\"", "src=\"" + itr.next());

                    LoggerUtil.infoLog(LinkGetter.class,"link generated is" + link);

                    linkList.add(link);
                }

            }
        }
        return linkList;
    }


}


