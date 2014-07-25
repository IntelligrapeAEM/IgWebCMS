package com.ig.igwebcms.services;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

@SlingServlet(methods = {"GET" ,"POST"} ,
                paths = {"/bin/backboneDemoTest"} ,
                generateComponent = false)
@Component(description = "This is Demo backend service for BackBone" ,
        immediate = true ,metatype = true ,enabled = true)
public class BackBoneDemoService extends SlingAllMethodsServlet {

    Logger logger = LoggerFactory.getLogger(BackBoneDemoService.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        try {
            JSONArray jsonArray = new JSONArray();

            JSONObject jObj1 = new JSONObject();
            jObj1.put("title" ,"test1");
            jObj1.put("price" ,150);
            jObj1.put("checked" ,false);

            JSONObject jObj2 = new JSONObject();
            jObj2.put("title" ,"test2");
            jObj2.put("price" ,250);
            jObj2.put("checked" ,false);

            JSONObject jObj3 = new JSONObject();
            jObj3.put("title", "Bankbone_test_data");
            jObj3.put("price" ,550);
            jObj3.put("checked" ,false);

            jsonArray.put(jObj1);
            jsonArray.put(jObj2);
            jsonArray.put(jObj3);

            JSONObject responseObj = new JSONObject();
            responseObj.put("list" ,jsonArray);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.print(responseObj);
            out.flush();
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage()+" "+e.getMessage());
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        doGet(request ,response);
    }
}