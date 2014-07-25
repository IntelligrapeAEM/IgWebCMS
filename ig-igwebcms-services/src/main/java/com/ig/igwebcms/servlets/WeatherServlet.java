package com.ig.igwebcms.servlets;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: intelligrape
 * Date: 6/15/14
 * Time: 11:38 PM
 * To change this template use File | Settings | File Templates.
 */

@SlingServlet(methods = {"GET" ,"POST"} , paths={"/bin/servlet/weather"},generateComponent = false)
@Component(immediate = true,enabled = true,metatype = false)
public class WeatherServlet extends SlingAllMethodsServlet{
private static final Logger logger = LoggerFactory.getLogger(WeatherServlet.class);
    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
            IOException {
        String latitude = request.getParameter("latitude");
        logger.info("deepaklatitude"+latitude);
        String longitude = request.getParameter("longitude");
        logger.info("long"+longitude);

    String url= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius=5000&types=food&name=harbour&sensor=false&key=AIzaSyA6M1EMn_KKGNIu89f6_YbwgKg2Cl1ih10";
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(url);
    String res;
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(3, false));

    try {
        int statusCode = client.executeMethod(method);
        logger.info("statusCode"+statusCode);
        if (statusCode != HttpStatus.SC_OK) {
            System.err.println("Method failed: " + method.getStatusLine());
        }
        byte[] responseBody = method.getResponseBody();
        res = new String(responseBody);
        response.getWriter().print(res);

    } catch (HttpException e) {
        logger.info("Fatal protocol violation: " + e.getMessage());
        e.printStackTrace();
    } catch (IOException e) {
        logger.info( "Fatal transport error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        method.releaseConnection();
    }

}
}