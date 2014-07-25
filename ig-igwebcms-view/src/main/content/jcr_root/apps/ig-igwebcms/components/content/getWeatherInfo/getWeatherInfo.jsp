<%--

  UpdateWeatherNodes component.

  component to create nodes inside etc

--%><%
%>
<%@ taglib uri="http://ig.com/igwebcms" prefix="ig" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
%>
<%@page session="false" %>
<%
%><%
    // TODO add you code here
%>

<cq:includeClientLib categories="commons.util.jsutil"/>

Weather Information of country

<%
    String country = properties.get("country", "");
    String state = properties.get("state", "");
    String city = properties.get("city", "");
    String temp = properties.get("temp", "");
    out.println(country);
    out.println("state " + state);
    out.println("and city " + city + " is " + temp);
%>

<ig:weatherInfo/>