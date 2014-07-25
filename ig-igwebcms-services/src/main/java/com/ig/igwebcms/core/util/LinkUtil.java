package com.ig.igwebcms.core.util;

import com.ig.igwebcms.core.constants.ApplicationConstants;
import com.ig.igwebcms.core.constants.FileExtConstants;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.security.AccessControlException;
import java.util.Locale;

/**
 * It identify that the resource at a given url is locked, is assets or is
 * access by a logged in user.
 */
public final class LinkUtil {

    /**
     * Hidden constructor.
     */
    private LinkUtil() {
        /*  NO CONSTRUCTOR*/
    }

    /**
     * This method returns if a link is locked for the logged in user.
     *
     * @param slingRequest the sling request
     * @param url          the url
     * @return boolean
     */
    public static boolean isLinkLocked(final SlingHttpServletRequest slingRequest, final String url) {

        boolean result = false;
        if (slingRequest != null && !StringUtils.isEmpty(url) && url.startsWith(ApplicationConstants.CONTENT_PATH)) {

            final String nodePath = StringUtils.removeEnd(url, FileExtConstants.DOT_HTML);

            final Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
            try {
                session.checkPermission(nodePath, ApplicationConstants.PERMISSION_READ);
            } catch (final AccessControlException accessControlException) {
                LoggerUtil.debugLog(LinkUtil.class, "Ignoring AccessControlException while checking permissions - " + url, accessControlException);
                result = true;
            } catch (final RepositoryException repositoryException) {
                LoggerUtil.debugLog(LinkUtil.class, "Ignoring RepositoryException while checking permissions - " + url, repositoryException);
            } catch (final Exception exception) {
                LoggerUtil.debugLog(LinkUtil.class, "Ignoring Exception while checking permissions - " + url, exception);
            }
        }
        return result;
    }

    /**
     * This method returns if a user is logged in.
     *
     * @param slingRequest the sling request
     * @return boolean
     */
    public static boolean isUserLoggedIn(final SlingHttpServletRequest slingRequest) {
        boolean isLoggedIn = false;
        if (slingRequest != null && !(slingRequest.getUserPrincipal().getName().toLowerCase(Locale.US)
                .equals(ApplicationConstants.AUTH_STATUS_ANONYMOUS.toLowerCase(Locale.US)))) {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    /**
     * Checks if is asset.
     *
     * @param url the url
     * @return true, if is asset
     */
    public static boolean isAsset(final String url) {

        return (url.startsWith(ApplicationConstants.DAMP_PATH)) ? true : false;
    }

    /**
     * Gets the external link status.
     *
     * @param slingRequest the sling request
     * @param tempUrl      the url
     * @return boolean.
     */
    public static boolean isExternalLink(final SlingHttpServletRequest slingRequest, final String tempUrl) {

        boolean result = false;
        String url = tempUrl;
        if (url.equalsIgnoreCase("#")) {
            result = false;
        } else {
            url = url.toLowerCase();
            final boolean isImage = (url.endsWith(FileExtConstants.DOT_PNG)
                    || url.endsWith(FileExtConstants.DOT_GIF) || url.endsWith(FileExtConstants.DOT_JPG)
                    || url.endsWith(FileExtConstants.DOT_JPEG) || url.endsWith(FileExtConstants.DOT_BMP)
                    || url.endsWith(FileExtConstants.DOT_TIF));
            final boolean isRepoPath = (url.startsWith(ApplicationConstants.DAMP_PATH)) || (url.startsWith(ApplicationConstants.CONTENT_PATH));
            result = (!isRepoPath && !(isImage)) ? true : false;

        }
        return result;
    }
}
