<%--

 Page Rendering Component

--%>

<%@include file="/libs/foundation/global.jsp" %>

<%@page session="false" %>

<!DOCTYPE html>

<html>
<head>
    <%@include file="/libs/wcm/core/components/init/init.jsp" %>

    <title>Weather</title>

</head><body>
    <cq:include path="par88" resourceType="/apps/ig-igwebcms/components/content/userinfo"/><br/><br/>


Weather Information

    <br/>
<cq:include path="par1" resourceType="/apps/ig-igwebcms/components/content/weatherInfo"/>



 <div id="basicMap"></div>

</body>
   

</html>