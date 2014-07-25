package com.ig.igwebcms.core.util;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.ig.igwebcms.core.constants.ApplicationConstants;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.scripting.SlingScriptHelper;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

/**
 * This Class Provide methods related to sling script resolution.
 * It also find the WCMMode for the sling request.
 * It also parse the Sling request url for getting different values.
 */
public final class RequestUtil {


    /**
     * Hidden constructor.
     */
    private RequestUtil() { /* NO CONSTRUCTOR */ }

    /**
     * Checks whether we are in edit mode (= authoring functions enabled).
     *
     * @param request The <code>SlingHttpServletRequest</code> upon which to determine the author mode.
     * @return true, if we are in author mode / false, otherwise
     */
    public static boolean isEditMode(final SlingHttpServletRequest request) {

        return (WCMMode.fromRequest(request) == WCMMode.EDIT || WCMMode.fromRequest(request) == WCMMode.DESIGN);
    }

    /**
     * Checks whether we are in preview mode.
     *
     * @param request The <code>SlingHttpServletRequest</code> upon which to determine the preview mode.
     * @return true, if we are in preview mode / false, otherwise
     */
    public static boolean isPreviewMode(final SlingHttpServletRequest request) {

        return (WCMMode.fromRequest(request) == WCMMode.PREVIEW);
    }

    /**
     * Checks whether we are in publish mode.
     *
     * @param request The <code>SlingHttpServletRequest</code> upon which to determine the publish mode.
     * @return true, if we are in publish mode / false, otherwise
     */
    public static boolean isPublishMode(final SlingHttpServletRequest request) {

        return (WCMMode.fromRequest(request) == WCMMode.DISABLED);
    }

    /**
     * Check if we are going through dispatcher.
     *
     * @param request Request object
     * @return True, if going through dispatcher
     */
    public static boolean isDispatcher(final SlingHttpServletRequest request) {

        final String dispatcher = request.getHeader(ApplicationConstants.SERVER_AGENT);
        return (StringUtils.isNotBlank(dispatcher) && ApplicationConstants.IS_DISPATCHER.equals(dispatcher));
    }

    /**
     * Check if selector set.
     *
     * @param request  Request object
     * @param selector Selector string
     * @return True, if set
     */
    public static Boolean hasSelector(final SlingHttpServletRequest request,
                                      final String selector) {

        List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
        return selectors.contains(selector);
    }

    /**
     * Get selectors list.
     *
     * @param request Request object
     * @return Selector list.
     */
    public static List<String> getSelectorList(final SlingHttpServletRequest request) {

        return Arrays.asList(request.getRequestPathInfo().getSelectors());
    }

    /**
     * Get selector as Dot(.) Seperated  string.
     *
     * @param request      Request object
     * @param defaultValue Default value (it accept null also)
     * @return Selector string or default value
     */
    public static String getSelectorAsString(final SlingHttpServletRequest request, final String defaultValue) {

        String output = defaultValue;
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors.length > 0) {
            output = "";
            for (String selector : selectors) {
                output += selector + ".";
            }

            output = output.substring(0, output.length() - 1);

        }
        return output;
    }

    /**
     * Get a single selector.
     *
     * @param request       Request object
     * @param selectorIndex Index of selector
     * @param defaultValue  Default value (it accept null also)
     * @return Selector string or default value
     */
    public static String getSelectorOnIndex(final SlingHttpServletRequest request,
                                            final int selectorIndex, final String defaultValue) {

        String selector = defaultValue;
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors.length > selectorIndex) {
            selector = selectors[selectorIndex];
        }
        return selector;
    }

    /**
     * Safely get all values of a parameter as a List of strings.
     *
     * @param request       Request object
     * @param parameterName Parameter name
     * @return List of parameter values
     */
    public static List<String> getParameterValues(final SlingHttpServletRequest request, final String parameterName) {

        List<String> values = new ArrayList<>();
        String[] valuesArr = request.getParameterValues(parameterName);
        if (valuesArr != null) {
            values = Arrays.asList(valuesArr);
        }
        return values;
    }

    /**
     * Safely get single value of a parameter as a strings.
     *
     * @param request       Request object
     * @param parameterName Parameter name
     * @return String value of parameter
     */
    public static String getParameterValue(final SlingHttpServletRequest request, final String parameterName) {

        return ((request.getParameter(parameterName) != null) ? request.getParameter(parameterName) : "");
    }

    /**
     * Safely get parameter value as integer.
     *
     * @param request       Request object
     * @param parameterName Parameter name
     * @param defaultValue  Default value
     * @return Int value
     */
    public static int getParameterIntValue(final SlingHttpServletRequest request, final String parameterName,
                                           final int defaultValue) {

        try {
            final String stringValue = request.getParameter(parameterName);
            if (stringValue != null) {
                return Integer.parseInt(stringValue);
            }
        } catch (NumberFormatException numberFormatException) {
            LoggerUtil.debugLog(RequestUtil.class, "Could not parse parameter '{}' as integer: {}",
                    parameterName, numberFormatException.getMessage());
        }
        return defaultValue;
    }

    /**
     * Safely get parameter value as boolean.
     *
     * @param request       Request object
     * @param parameterName Parameter name
     * @param defaultValue  Default value
     * @return Boolean value
     */
    public static boolean getParameterBooleanValue(final SlingHttpServletRequest request, final String parameterName,
                                                   final boolean defaultValue) {

        final String stringValue = request.getParameter(parameterName);
        return (ApplicationConstants.TRUE.equals(stringValue)) ? true : defaultValue;
    }

    /**
     * safely get parameter map as value for all parameters or parameters selected by you.
     *
     * @param request    Request object.
     * @param paramNames list of parameter names.
     * @return <code>Map<String,String[]></code> with parameter and there values.
     */
    public static Map<String, String[]> getParameterMap(final SlingHttpServletRequest request, final List<String> paramNames) {

        Map<String, String[]> map = new HashMap<>();
        if (paramNames == null || paramNames.size() == 0) {
            return request.getParameterMap();
        } else {
            for (String parameter : paramNames) {
                map.put(parameter, request.getParameterValues(parameter));
            }
            return map;
        }
    }

    /**
     * Safely get all parameter with all of its values as map for a request.
     *
     * @param request Request object
     * @return <code>Map<String,String[]></code> with parameter and there values.
     */
    public static Map<String, String[]> getParameterMap(final SlingHttpServletRequest request) {

        Map<String, String[]> map = new HashMap<String, String[]>();
        return request.getParameterMap();
    }

    /**
     * Safely get all parameter with single value as map for a request.
     *
     * @param request Request object.
     * @return <code>Map<String,String></code> with parameter and there values.
     */
    public static Map<String, String> getParameterWithSingleValueMap(final SlingHttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> parameterNames = request.getParameterNames();
        String parameterName = "", parameterValue = "";
        while (parameterNames.hasMoreElements()) {
            parameterName = parameterNames.nextElement();
            parameterValue = request.getParameter(parameterName);
            map.put(parameterName, parameterValue);
        }
        return map;
    }

    /**
     * Safely get all parameter of a request or selected parameters with single value as map for a request.
     *
     * @param request    Request object.
     * @param paramNames list of desired parameters.
     * @return <code>Map<String,String></code> with parameter and there values.
     */
    public static Map<String, String> getParameterWithSingleValueMap(final SlingHttpServletRequest request, final List<String> paramNames) {

        Map<String, String> map = new HashMap<String, String>();
        if (paramNames == null || paramNames.size() == 0) {
            Enumeration<String> parameterNames = request.getParameterNames();
            String parameterName = "", parameterValue = "";
            while (parameterNames.hasMoreElements()) {
                parameterName = parameterNames.nextElement();
                parameterValue = request.getParameter(parameterName);
                map.put(parameterName, parameterValue);
            }
        } else {
            for (String parameter : paramNames) {
                map.put(parameter, request.getParameter(parameter));
            }
        }
        return map;
    }

    /**
     * Safely get all parameter name list.
     *
     * @param request Request object
     * @return <code>List<String></code> List of parameter names.
     */
    public static List<String> getParameterList(final SlingHttpServletRequest request) {
        Enumeration parameters = request.getParameterNames();
        List<String> parameterList = new ArrayList<String>();
        while (parameters.hasMoreElements()) {
            parameterList.add(parameters.nextElement().toString());
        }
        return parameterList;
    }

    /**
     * Check if resource actually exists (disregarding permissions).
     *
     * @param request Request object
     * @param sling   Sling script helper
     * @return True, if resource exists
     */
    public static boolean isResourceExistsForAdmin(final SlingHttpServletRequest request,
                                                   final SlingScriptHelper sling) {
        ResourceResolver adminResourceResolver = null;
        try {
            ResourceResolverFactory resourceResolverFactory = sling.getService(ResourceResolverFactory.class);
            adminResourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            Resource adminResource = adminResourceResolver.resolve(request, request.getRequestPathInfo().getResourcePath());
            return true;
        } catch (Exception exception) {
            LoggerUtil.debugLog(RequestUtil.class, "Could not login administrative: ", exception.getMessage());
        } finally {
            if (adminResourceResolver != null) {
                adminResourceResolver.close();
            }
        }
        return false;
    }

    /**
     * Gets the plain suffix, strips the leading '/'.
     *
     * @param request the request
     * @return the plain suffix
     */
    public static String getPlainSuffix(final SlingHttpServletRequest request) {

        String suffix = "";
        try {
            suffix = request.getRequestPathInfo().getSuffix();
            if (suffix.startsWith(ApplicationConstants.SLASH)) {
                suffix = StringUtils.substringAfter(suffix, ApplicationConstants.SLASH);
            }
        } catch (Exception e) {
            LoggerUtil.infoLog(RequestUtil.class, "error while reading suffix of the given url" + e.toString());
        }
        return suffix;
    }

    /**
     * Get extension from request. Also retrieve the extension from node paths
     * which technically don't have an extension (as in DAM).
     *
     * @param request Sling request object
     * @return Extension string
     */
    public static String getExtension(final SlingHttpServletRequest request) {

        if (request.getRequestPathInfo().getExtension() != null) {
            return StringUtils.lowerCase(request.getRequestPathInfo().getExtension());
        } else {
            String path = request.getRequestPathInfo().getResourcePath();
            return StringUtils.substringAfterLast(path, ".");
        }
    }

    /**
     * Retrieve the page containing the component from a request is coming.
     *
     * @param request Request object
     * @return Page Page containing component.
     */
    public static Page getCurrentPage(final SlingHttpServletRequest request) {

        final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        final Resource resource = request.getResource();
        return pageManager.getContainingPage(resource);
    }

    /**
     * Generate a list of query parameters from a key-value map.
     *
     * @param queryParameterMap   the key-value map
     * @param prependQuestionMark if true the query string will contain a leading quesstion mark
     * @return the URL encoded query string
     * @throws java.io.UnsupportedEncodingException
     *          UnsupportedEncodingException
     */
    public static String createQueryString(final Map<String, Object> queryParameterMap,
                                           final boolean prependQuestionMark) throws UnsupportedEncodingException {

        final String queryParamFormat = "%s=%s";
        StringBuilder sb = new StringBuilder();

        if (queryParameterMap != null && queryParameterMap.size() > 0) {
            int index = 0;
            for (Map.Entry<String, Object> parameter : queryParameterMap.entrySet()) {
                String parameterStringValue = parameter.getValue().toString();
                String queryParameter = String.format(queryParamFormat,
                        parameter.getKey(), URLEncoder.encode(parameterStringValue, ApplicationConstants.ENCODING_FORMAT_UTF8));

                if (index == 0 && prependQuestionMark) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(queryParameter);
                index++;
            }
        }
        return sb.toString();
    }

    /**
     * Get the IP Address from the request.
     *
     * @param request SlingHttpServletRequest
     * @return The IP as String
     */
    public static String getIP(final SlingHttpServletRequest request) {

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            String[] forwardIps = xForwardedFor.split(",");

            for (final String forwardIp : forwardIps) {
                try {
                    InetAddress clientIp = InetAddress.getByName(forwardIp.trim());
                    return clientIp.toString();
                } catch (UnknownHostException unknownHostException) {
                    LoggerUtil.infoLog(RequestUtil.class, "received X-Forwarded-For header containing unparseable value {}");
                }
            }
        }
        return request.getRemoteAddr();
    }
}
