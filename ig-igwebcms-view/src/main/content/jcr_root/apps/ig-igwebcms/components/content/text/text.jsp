<%@page session="false" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/apps/ig-igwebcms/global.jsp"%>
<%@taglib prefix="bodyParser" uri="http://ig.com/igwebcms" %>

<bodyParser:parse>
    <cq:text property="text" escapeXml="true"
             placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
</bodyParser:parse>
