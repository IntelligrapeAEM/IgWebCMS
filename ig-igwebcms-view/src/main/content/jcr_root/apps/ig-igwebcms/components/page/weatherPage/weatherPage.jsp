<%--

  Banded component.

  This is generic responsive template . Banded

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
<!DOCTYPE html>
<!--[if IE 9]><html class="lt-ie10" lang="en" > <![endif]-->
<html>
<head>
    <%@include file="/libs/wcm/core/components/init/init.jsp" %>

    <title>Foundation Template | Banded</title>
    <!-- <ig:clientLib>
        <cq:includeClientLib categories="ig.clientlib1"/> </ig:clientLib>   -->
</head>
<body>
Weather display component
<cq:include path="par" resourceType="foundation/components/parsys"/>
<%
    /*   WeatherService weatherService=sling.getService(WeatherService.class);
       String s=weatherService.getWeather("Delhi");
       out.println("received string"+s);*/
%>


</body>
</html>