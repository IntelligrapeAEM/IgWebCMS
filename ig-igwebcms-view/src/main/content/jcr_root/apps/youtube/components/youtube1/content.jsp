<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================



--%>
<%@ include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/hideeditok.jsp"%>
<%@page session="false" contentType="text/html"
        pageEncoding="utf-8"
        import="org.apache.sling.api.resource.ValueMap,
                javax.jcr.Node,
                com.adobe.granite.crypto.CryptoSupport"%>
<%@ page import="javax.jcr.Session" %>
<%@page import="com.day.cq.i18n.I18n" %>

<%
%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>


<%
    ValueMap youtubeValue = resource.adaptTo(ValueMap.class);
    Node youtubeNode = resource.adaptTo(Node.class);
    I18n i18n = new I18n(slingRequest.getResourceBundle(currentPage.getLanguage(false)));
    String customerKey="";

    if(youtubeValue!=null){

        customerKey = youtubeValue.get("customerkey", "");
        customerSecret = youtubeValue.get("customersecret", "");
        String refreshToken = youtubeValue.get("refreshtoken", "");
        CryptoSupport cryptoSupport = sling.getService(CryptoSupport.class);



    }
    Session resourceSession = resource.getResourceResolver().adaptTo(Session.class);
    resourceSession.save();

%>
<div>
    <div>
        <h3><%= i18n.get("Basic Settings")%></h3>
        <ul>
            <li>
                <div class="li-bullet">
                    <strong><%= i18n.get("Customer Key:")%></strong>
                    <%= customerKey.length() > 20 ? customerKey.substring(0, 20) +  "..." : customerKey %>
                </div>
            </li>

            <li>
                <div class="li-bullet">
                    <strong><%= i18n.get("Channel Id")%> hello</strong>
                    <%= youtubeValue.get("channelID", "") %>
                </div>
            </li>


            <li class="config-successful-message when-config-successful" style="display: none"><%= i18n.get("Salesforce configuration is successful.")%><br>
                <%=i18n.get("Please apply the configuration to your")%> <a href="/siteadmin"><%= i18n.get("website")%></a> <%= i18n.get("You can now add Salesforce to your site")%></li>

        </ul>
    </div>

</div>